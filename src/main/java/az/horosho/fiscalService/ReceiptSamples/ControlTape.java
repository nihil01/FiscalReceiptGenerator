package az.horosho.fiscalService.ReceiptSamples;

import az.horosho.PrinterService;
import az.horosho.fiscalService.ReceiptActions;
import az.horosho.fiscalService.responses.*;
import az.horosho.fiscalService.responses.requests.ConcreteRequestStructure;
import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction;

import javax.print.PrintException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControlTape implements Common{
    private final ConcreteResponseStructure<GetControlTape> responseDocument;
    private byte[][] tapeReceiptStructure;
    private ArrayList<Byte[]> tapeReceipt;

    public ControlTape(ConcreteResponseStructure<GetControlTape> responseDocument) {
        this.responseDocument = responseDocument;
        initializeStructure();
    }

    private void initializeStructure(){
        GetInfo getInfoData = getInfoData();
        HeaderPart headerPart = generateHeaderPartControlTape("Nezaret lenti", responseDocument);
        tapeReceipt = new ArrayList<>();
        iterateOverItems(responseDocument.getData().getTape());

        this.tapeReceiptStructure = new byte[][]{
            concat(headerPart.getHeader()),
                new byte[]{	0x1B, 0x64, 1},

                // Set font size to small (height and width reduced)
                new byte[]{0x1B,0x21,0x01},

                "******************************************".getBytes(),
                new byte[]{0x0A},
                ("Novbenin acilma vaxti: " + responseDocument.getData().getShiftOpenAtUtc()).getBytes(),
                new byte[]{0x0A},
                ("Novbenin baglanma vaxti: " +
                        (responseDocument.getData().getShiftCloseAtUtc() == null ? "NOVBE AKTIVDIR"
                                : responseDocument.getData().getShiftCloseAtUtc()))
                        .getBytes(),
                new byte[]{0x0A},
                ("Hesabatin alinma vaxti: " + responseDocument.getData().getCreatedAtUtc()).getBytes(),
                new byte[]{0x0A},
                ("Cek sayi: " + responseDocument.getData().getDocuments_quantity()).getBytes(),
                new byte[]{0x0A},
                "******************************************".getBytes(),
                new byte[]{0x0A},
                new byte[]{0x0A},

                "Emeliyyat  Emtee sayi  Cemi vergi  Toplam".getBytes(),
                new byte[]{0x0A},
                "******************************************".getBytes(),
                new byte[]{0x0A},
                toByteArray(concat(tapeReceipt)),
                new byte[]{0x0A},
                "******************************************".getBytes(StandardCharsets.UTF_8),
                // Feed 1 line
                new byte[]{0x0A},
                String.format("Kassa aparatin modeli: %s", getInfoData.getCashregister_model()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Kassa aparatin seriya nomresi: %s", getInfoData.getCashbox_factory_number()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("NMQ-nun qeydiyyat nomresi: %s", getInfoData.getCashbox_tax_number()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                new byte[]{0x1B, 0x61, 0x01},
                "www.e-kassa.az".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

        };
    }

    private void iterateOverItems(List<Tape> tape){
        System.out.println(Arrays.toString(tape.toArray()));
        if (tape.isEmpty()){
            byte[][] goodsArray = new byte[][]{
                    "Melumat yoxdur !!".getBytes(),
                    new byte[]{0x0A},
            };

            tapeReceipt.add(toByteArray(concat(goodsArray)));
            return;
        }

        for (Tape el: tape){
            byte[][] goodsArray = {
                "=================================================".getBytes(),
                new byte[]{0x0A},
                substituteOperationName(el.getType()).getBytes(),
                new byte[]{0x0A},
                "Tarix: %s".formatted(el.getCreatedAtUtc()).getBytes(),
                new byte[]{0x1B, 0x61, 0x01},
                " %d  %.2f  %.2f".formatted(el.getItem_count(),
                        el.getTotal_vat(), el.getTotal_sum()).getBytes(),
                new byte[]{0x0A},
                ("Status: " + (el.isDelivered() ? "gonderilib" : "gonderilmeyib")).getBytes(),
                new byte[]{0x0A},
                "=================================================".getBytes(),
            };
            tapeReceipt.add(toByteArray(concat(goodsArray)));
        }
    }

    private String substituteOperationName(String operationName){
        return switch (operationName) {
            case "sale" -> "Satis";
            case "deposit" -> "Medaxil";
            case "withdraw" -> "Mexaric";
            case "correction" -> "Korreksiya";
            case "prepay" -> "Avans odenisi";
            case "creditpay" -> "Nisye odenisi";
            case "zreport" -> "Z Hesabati";
            case "money_back" -> "Geri qaytarma";
            case "rollback" -> "Legv";
            default -> operationName;
        };
    }

    public void generate(){
        PrinterService printerService = new PrinterService();
        try {
            printerService.printReceipt(concat(tapeReceiptStructure));
        } catch (PrintException e) {
            System.err.println("UNABLE TO PRINT Z REPORT RECEIPT");
            throw new RuntimeException(e);
        }
    }
}
