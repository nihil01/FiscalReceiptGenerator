package az.horosho.fiscalService.ReceiptSamples;

import az.horosho.VariablesForRequestProcessing;
import az.horosho.fiscalService.responses.ConcreteResponseStructure;
import az.horosho.fiscalService.responses.CreateDocument;
import az.horosho.fiscalService.responses.GetInfo;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class HeaderPart {
    private final GetInfo getInfo;
    private final LocalDateTime dateTime = LocalDateTime.now();
    private final String dateNow = String.format("Tarix: %d.%d.%d", dateTime.getDayOfMonth(), dateTime.getMonthValue(), dateTime.getYear());
    private final String timeNow = String.format("Saat: %d:%d:%d", dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
    private final VariablesForRequestProcessing vars = new VariablesForRequestProcessing();
    private final String chequeType;
    private final int chequeNumber;

    public HeaderPart(GetInfo getInfo, String chequeType, int chequeNumber) {
        this.getInfo = getInfo;
        this.chequeNumber = chequeNumber;
        this.chequeType = chequeType;
    }

    public byte[][] getHeader() {
        // Initialize printer
        return new byte[][]{
                new byte[]{0x1B, 0x40},                             // Initialize printer
                new byte[]{0x1B, 0x21, 0x01},                       // Center alignment
                getInfo.getObject_address().getBytes(StandardCharsets.UTF_8),
                getInfo.getCompany_name().getBytes(StandardCharsets.UTF_8),
                getInfo.getCompany_tax_number().getBytes(StandardCharsets.UTF_8),
                getInfo.getObject_tax_number().getBytes(StandardCharsets.UTF_8),
                getInfo.getLast_doc_number().getBytes(StandardCharsets.UTF_8),
                new byte[]{0x1B, 0x64, 0x02},                       // Feed 2 lines
                new byte[]{0x1D, 0x56, 0x42, 0x00},                 // Partial cut
                new byte[]{0x1B, 0x32, 0x02},

                new byte[]{0x1B, 0x40},
                // Set font size to small (height and width reduced)
                new byte[]{0x1B, 0x21, 0x01},
                // Center align
                new byte[]{0x1B, 0x61, 0x01},
                chequeType.getBytes(StandardCharsets.UTF_8),
                // Next line
                new byte[]{0x0A},
                // Left align
                new byte[]{0x1B, 0x61, 0x00},
                String.format("Cek nomresi #%d", chequeNumber).getBytes(StandardCharsets.UTF_8),
                // Next line
                new byte[]{0x0A},
                String.format("Magaza: %s", vars.getPreferences("shop")).getBytes(StandardCharsets.UTF_8),
                // Next line
                new byte[]{0x0A},
                String.format("Kassa nomresi: %s", vars.getPreferences("cashbox")).getBytes(StandardCharsets.UTF_8),
                // Next line
                new byte[]{0x0A},
                // Right align
                new byte[]{0x1B, 0x61, 0x02},
                dateNow.getBytes(StandardCharsets.UTF_8),
                // Next line
                new byte[]{0x0A},
                timeNow.getBytes(StandardCharsets.UTF_8),
                // Next line
                new byte[]{0x0A},
                // Left align
                new byte[]{0x1B, 0x61, 0x00},
                String.format("Kassir: %s", vars.getPreferences("cashier")).getBytes(StandardCharsets.UTF_8),
                // Next line
                new byte[]{0x0A},
                // Set font size to normal
                new byte[]{0x1B, 0x21, 0x00},
                "================================".getBytes()
        };
    }
}
