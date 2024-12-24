package az.horosho.fiscalService.ReceiptSamples;

import az.horosho.PrinterService;
import az.horosho.fiscalService.ReceiptActions;
import az.horosho.fiscalService.responses.ConcreteResponseStructure;
import az.horosho.fiscalService.responses.CreateDocument;
import az.horosho.fiscalService.responses.GetInfo;
import az.horosho.fiscalService.responses.LastDocument;
import az.horosho.fiscalService.responses.requests.ConcreteRequestStructure;
import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction;

import javax.print.PrintException;
import java.nio.charset.StandardCharsets;

public class Correction implements Common{
    //Original receipt
    private ConcreteRequestStructure<CreateDocumentTransaction> requestDocument;
    private ConcreteResponseStructure<CreateDocument> responseDocument;

    //Last doc receipt
    private String fiscalIDForLastDocument;
    private LastDocument requestLastDocument;

    private byte[][] correctionReceiptStructure;

    public Correction(ConcreteRequestStructure<CreateDocumentTransaction> requestDocument,
                      ConcreteResponseStructure<CreateDocument> responseDocument) {
        this.requestDocument = requestDocument;
        this.responseDocument = responseDocument;
        initializeOriginalDocStructure();
    }

    public Correction(LastDocument requestDocument,
                      String fiscalID) {
        this.requestLastDocument = requestDocument;
        this.fiscalIDForLastDocument = fiscalID;
        initializeLastDocStructure();
    }

    private void initializeOriginalDocStructure() {
        GetInfo getInfoData = getInfoData();
        HeaderPart headerPart = generateHeaderPartCreateDocument("Korreksiya ceki", responseDocument);

        this.correctionReceiptStructure = new byte[][] {
                concat(headerPart.getHeader()),
                // Feed 2 lines
                new byte[]{0x1B, 0x64, 1},

                // Set font size to small (height and width reduced)
                new byte[]{0x1B, 0x21, 0x01},
                "******************************************".getBytes(),
                new byte[]{0x0A},
                ("Birinci emeliyyat tarixi: " + requestDocument.getParameters().getData().getFirstOperationAtUtc()).getBytes(),
                new byte[]{0x0A},
                ("Sonuncu emeliyyat tarixi: " + requestDocument.getParameters().getData().getFirstOperationAtUtc()).getBytes(),
                new byte[]{0x0A},
                "******************************************".getBytes(),
                new byte[]{0x0A},
                "Korreksiya meblegi:                         %.2f".formatted(requestDocument.getParameters().getData().getSum()).getBytes(),
                new byte[]{0x0A},
                "EDV: %.2f".formatted(requestDocument.getParameters().getData().getVatAmounts().getFirst().getVatPercent()).getBytes(),
                new byte[]{0x0A},
                "Yekun EDV: %.2f".formatted(
                        (requestDocument.getParameters().getData().getVatAmounts().getFirst().getVatSum() * requestDocument.getParameters().getData().getVatAmounts().getFirst().getVatPercent()) /
                                (requestDocument.getParameters().getData().getVatAmounts().getFirst().getVatPercent() + 100)).getBytes(),
                new byte[]{0x0A},
                "******************************************".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                "Odenis usulu:".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                ("Negdsiz: " + requestDocument.getParameters().getData().getCashlessSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                ("Negd: " + requestDocument.getParameters().getData().getCashSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Odenilib negd AZN:       %.2f", requestDocument.getParameters().getData().getIncomingSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Qaliq qaytarilib nagd AZN:       %.2f", requestDocument.getParameters().getData().getChangeSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Odenildi pul almadan(bonus karti):      %.2f", requestDocument.getParameters().getData().getBonusSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                "******************************************".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Novbe erzinde vurulmus cek sayi: %d", responseDocument.getData().getShift_document_number()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Kassa aparatin modeli: %s", getInfoData.getCashregister_model()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Kassa aparatin seriya nomresi: %s", getInfoData.getCashbox_factory_number()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Fiscal ID: %s", responseDocument.getData().getShort_document_id()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("NMQ-nun qeydiyyat nomresi: %s", getInfoData.getCashbox_tax_number()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                new byte[]{0x1B, 0x61, 0x01},
                "www.e-kassa.az".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
        };
    }

    private void initializeLastDocStructure() {
        GetInfo getInfoData = getInfoData();
        HeaderPart headerPart = generateHeaderPartCreateDocument("Korreksiya ceki (Get Last Document)", null);

        this.correctionReceiptStructure = new byte[][] {
                concat(headerPart.getHeader()),
                // Feed 2 lines
                new byte[]{0x1B, 0x64, 1},

                // Set font size to small (height and width reduced)
                new byte[]{0x1B, 0x21, 0x01},
                "******************************************".getBytes(),
                new byte[]{0x0A},
                ("Birinci emeliyyat tarixi: " + requestLastDocument.getFirstOperationAtUtc()).getBytes(),
                new byte[]{0x0A},
                ("Sonuncu emeliyyat tarixi: " + requestLastDocument.getFirstOperationAtUtc()).getBytes(),
                new byte[]{0x0A},
                "******************************************".getBytes(),
                new byte[]{0x0A},
                "Korreksiya meblegi:                         %.2f".formatted(requestLastDocument.getSum()).getBytes(),
                new byte[]{0x0A},
                "EDV: %.2f".formatted(requestLastDocument.getVatAmounts().getFirst().getVatPercent()).getBytes(),
                new byte[]{0x0A},
                "Yekun EDV: %.2f".formatted(
                        (requestLastDocument.getVatAmounts().getFirst().getVatSum() * requestLastDocument.getVatAmounts().getFirst().getVatPercent()) /
                                (requestLastDocument.getVatAmounts().getFirst().getVatPercent() + 100)).getBytes(),
                new byte[]{0x0A},
                "******************************************".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                "Odenis usulu:".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                ("Negdsiz: " + requestLastDocument.getCashlessSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                ("Negd: " + requestLastDocument.getCashSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Odenilib negd AZN:       %.2f", requestLastDocument.getIncomingSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Qaliq qaytarilib nagd AZN:       %.2f", requestLastDocument.getChangeSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Odenildi pul almadan(bonus karti):      %.2f", requestLastDocument.getBonusSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                "******************************************".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Novbe erzinde vurulmus cek sayi: %s", "YADDA SAXLAMAYIB").getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Kassa aparatin modeli: %s", getInfoData.getCashregister_model()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Kassa aparatin seriya nomresi: %s", getInfoData.getCashbox_factory_number()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Fiscal ID: %s", fiscalIDForLastDocument).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("NMQ-nun qeydiyyat nomresi: %s", getInfoData.getCashbox_tax_number()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                new byte[]{0x1B, 0x61, 0x01},
                "www.e-kassa.az".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
        };
    }

    public void generate(){
        PrinterService printerService = new PrinterService();
        try {
            printerService.printReceipt(concat(correctionReceiptStructure));
            if (fiscalIDForLastDocument != null) {
                ReceiptActions.generateFiscalQRCode(fiscalIDForLastDocument);
                return;
            }
            ReceiptActions.generateFiscalQRCode(responseDocument.getData().getShort_document_id());
        } catch (PrintException e) {
            System.err.println("UNABLE TO PRINT Z REPORT RECEIPT");
            throw new RuntimeException(e);
        }
    }

}
