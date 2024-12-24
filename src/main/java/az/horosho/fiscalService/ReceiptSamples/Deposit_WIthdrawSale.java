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

public class Deposit_WIthdrawSale implements Common{
    private ConcreteRequestStructure<CreateDocumentTransaction> requestDocument;
    private ConcreteResponseStructure<CreateDocument> responseDocument;
    private final DocType docType;

    private LastDocument requestLastDocument;
    private String fiscalIDForLastDocument;

    private byte[][] depositSaleReceiptStructure;
    public enum DocType {WITHDRAW, DEPOSIT}

    public Deposit_WIthdrawSale(ConcreteRequestStructure<CreateDocumentTransaction> requestDocument,
                                ConcreteResponseStructure<CreateDocument> responseDocument, DocType docType) {
        this.requestDocument = requestDocument;
        this.responseDocument = responseDocument;
        this.docType = docType;
        initializeOriginalStructure();
    }

    public Deposit_WIthdrawSale(LastDocument requestLastDocument, String fiscalIDForLastDocument, DocType docType) {
        this.requestLastDocument = requestLastDocument;
        this.fiscalIDForLastDocument = fiscalIDForLastDocument;
        this.docType = docType;
        initializeLastDocStructure();
    }

    public void initializeOriginalStructure() {
        GetInfo getInfoData = getInfoData();
        String reportName;
        reportName = this.docType == DocType.WITHDRAW ? "Mexaric ceki" : "Medaxil ceki";

        HeaderPart headerPart = generateHeaderPartCreateDocument(reportName, responseDocument);

        if (docType == DocType.DEPOSIT) {
            this.depositSaleReceiptStructure = new byte[][]{
                    concat(headerPart.getHeader()),
                    // Feed 2 lines
                    new byte[]{0x1B, 0x64, 1},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B, 0x21, 0x01},
                    // Left align
                    new byte[]{0x1B, 0x61, 0x00},
                    "Medaxil meblegi:            %.2f".formatted(requestDocument.getParameters().getData().getSum()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Yekun mebleg:            %.2f".formatted(requestDocument.getParameters().getData().getSum()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Medaxil usulu:".getBytes(),
                    new byte[]{0x0A},
                    "Nagd:                         %.2f".formatted(requestDocument.getParameters().getData().getSum()).getBytes(),
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
        }else {
            this.depositSaleReceiptStructure = new byte[][]{
                    concat(headerPart.getHeader()),
                    // Feed 2 lines
                    new byte[]{0x1B, 0x64, 1},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B, 0x21, 0x01},
                    // Left align
                    new byte[]{0x1B, 0x61, 0x00},
                    "Mexaric meblegi:            %.2f".formatted(requestDocument.getParameters().getData().getSum()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Yekun mebleg:            %.2f".formatted(requestDocument.getParameters().getData().getSum()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Mexaric usulu:".getBytes(),
                    new byte[]{0x0A},
                    "Nagd:                         %.2f".formatted(requestDocument.getParameters().getData().getSum()).getBytes(),
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
    }

    public void initializeLastDocStructure() {
        GetInfo getInfoData = getInfoData();
        String reportName;
        reportName = this.docType == DocType.WITHDRAW ? "Mexaric ceki(Get Last Document)" :
                "Medaxil ceki(Get Last Document)";

        HeaderPart headerPart = generateHeaderPartCreateDocument(reportName, responseDocument);

        if (docType == DocType.DEPOSIT) {
            this.depositSaleReceiptStructure = new byte[][]{
                    concat(headerPart.getHeader()),
                    // Feed 2 lines
                    new byte[]{0x1B, 0x64, 1},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B, 0x21, 0x01},
                    // Left align
                    new byte[]{0x1B, 0x61, 0x00},
                    "Medaxil meblegi:            %.2f".formatted(requestLastDocument.getSum()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Yekun mebleg:            %.2f".formatted(requestLastDocument.getSum()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Medaxil usulu:".getBytes(),
                    new byte[]{0x0A},
                    "Nagd:                         %.2f".formatted(requestLastDocument.getSum()).getBytes(),
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
        }else {
            this.depositSaleReceiptStructure = new byte[][]{
                    concat(headerPart.getHeader()),
                    // Feed 2 lines
                    new byte[]{0x1B, 0x64, 1},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B, 0x21, 0x01},
                    // Left align
                    new byte[]{0x1B, 0x61, 0x00},
                    "Mexaric meblegi:            %.2f".formatted(requestLastDocument.getSum()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Yekun mebleg:            %.2f".formatted(requestLastDocument.getSum()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Mexaric usulu:".getBytes(),
                    new byte[]{0x0A},
                    "Nagd:                         %.2f".formatted(requestLastDocument.getSum()).getBytes(),
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
    }

    public void generate() {
        PrinterService printerService = new PrinterService();
        try {
            printerService.printReceipt(concat(depositSaleReceiptStructure));

            if (fiscalIDForLastDocument != null){
                ReceiptActions.generateFiscalQRCode(fiscalIDForLastDocument);
            }

            ReceiptActions.generateFiscalQRCode(responseDocument.getData().getShort_document_id());
        } catch (PrintException e) {
            System.err.println("UNABLE TO PRINT Z REPORT RECEIPT");
            throw new RuntimeException(e);
        }
    }
}
