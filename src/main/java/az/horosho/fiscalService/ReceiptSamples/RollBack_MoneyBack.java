package az.horosho.fiscalService.ReceiptSamples;

import az.horosho.PrinterService;
import az.horosho.fiscalService.ReceiptActions;
import az.horosho.fiscalService.responses.*;
import az.horosho.fiscalService.responses.requests.ConcreteRequestStructure;
import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction;

import javax.print.PrintException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RollBack_MoneyBack implements Common{

    public enum Type{
        MoneyBack,
        RollBack
    }

    //Original receipt
    private ConcreteRequestStructure<CreateDocumentTransaction> requestDocument;
    private ConcreteResponseStructure<CreateDocument> responseDocument;

    //Last doc receipt
    private String fiscalIDForLastDocument;
    private LastDocument requestLastDocument;

    private byte[][] moneyBackReceiptStructure;
    ArrayList<Byte[]> byteList = new ArrayList<>();
    private final Type type;

    public RollBack_MoneyBack(Type type,ConcreteRequestStructure<CreateDocumentTransaction> requestDocument,
                              ConcreteResponseStructure<CreateDocument> responseDocument) {
        this.requestDocument = requestDocument;
        this.responseDocument = responseDocument;
        this.type = type;
        initializeOriginalDocStructure();
    }

    public RollBack_MoneyBack(Type type, LastDocument requestDocument,
                              String fiscalID) {
        this.requestLastDocument = requestDocument;
        this.fiscalIDForLastDocument = fiscalID;
        this.type = type;
        initializeLastDocStructure();
    }

    private void initializeOriginalDocStructure() {
        GetInfo getInfoData = getInfoData();
        String resultingDoc;
        resultingDoc = this.type == Type.MoneyBack ? "Geri Qaytarma ceki" : "Legv Etme ceki";

        HeaderPart headerPart = generateHeaderPartCreateDocument(resultingDoc, responseDocument);
        CreateDocumentTransaction.Data calcVariables = requestDocument.getParameters().getData();
        double[] vatResults = calcVATPercentResult(calcVariables.getVatAmounts());

        if (vatResults == null) return;

        this.moneyBackReceiptStructure = new byte[][] {
                concat(headerPart.getHeader()),
                // Feed 2 lines
                new byte[]{0x1B, 0x64, 1},

                // Set font size to small (height and width reduced)
                new byte[]{0x1B, 0x21, 0x01},
                "******************************************".getBytes(),
                new byte[]{0x0A},
                ("Satis cekinin Fiscal ID-si: " + requestDocument.getParameters().getData().getParentDocument()).getBytes(),
                new byte[]{0x0A},
                "******************************************".getBytes(),

                new byte[]{0x1B, 0x61, 0x00},
                "Mehsulun adi".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                //Tabulation
                new byte[]{0x1B, 0x61, 0x00}, // Левое выравнивание (на всякий случай)
                "Miq.          Qiy.-AZN          Top.-AZN".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                // Feed 1 line
                new byte[]{	0x1B, 0x64, 1},

                iterateThroughItems(requestDocument.getParameters().getData().getItems()),

                new byte[]{0x0A},
                String.format("YEKUN MEBLEG:       %.2f", calcVariables.getSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("EDV-den AZAD:       %.2f", vatResults[2]).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("EDV %.2f:       %.2f",vatResults[1],vatResults[0]).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("YEKUN VERGI:       %.2f",
                        (calcVariables.getSum() * vatResults[1])/(100 + vatResults[1]))
                        .getBytes(StandardCharsets.UTF_8),
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
                String.format("Avans (beh):   %.2f", requestDocument.getParameters().getData().getPrepaymentSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Nisye:   %.2f", requestDocument.getParameters().getData().getCreditSum()).getBytes(StandardCharsets.UTF_8),
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
        String resultingDoc;
        resultingDoc = this.type == Type.MoneyBack ? "Geri Qaytarma ceki(Get Last Document)"
                : "Legv Etme ceki(Get Last Document)";
        HeaderPart headerPart = generateHeaderPartCreateDocument(resultingDoc, responseDocument);
        double[] vatResults = calcVATPercentResult(requestLastDocument.getVatAmounts());
        if (vatResults == null) return;

        this.moneyBackReceiptStructure = new byte[][] {
                concat(headerPart.getHeader()),
                // Feed 2 lines
                new byte[]{0x1B, 0x64, 1},

                // Set font size to small (height and width reduced)
                new byte[]{0x1B, 0x21, 0x01},
                "******************************************".getBytes(),
                new byte[]{0x0A},
                ("Satis cekinin Fiscal ID-si: " + requestLastDocument.getParentDocument()).getBytes(),
                new byte[]{0x0A},
                "******************************************".getBytes(),

                new byte[]{0x1B, 0x61, 0x00},
                "Mehsulun adi".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                //Tabulation
                new byte[]{0x1B, 0x61, 0x00}, // Левое выравнивание (на всякий случай)
                "Miq.          Qiy.-AZN          Top.-AZN".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                // Feed 1 line
                new byte[]{	0x1B, 0x64, 1},

                iterateThroughItems(requestLastDocument.getItems()),

                new byte[]{0x0A},
                String.format("YEKUN MEBLEG:       %.2f", requestLastDocument.getSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("EDV-den AZAD:       %.2f", vatResults[2]).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("EDV %.2f:       %.2f",vatResults[1],vatResults[0]).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("YEKUN VERGI:       %.2f",
                                (requestLastDocument.getSum() * vatResults[1])/(100 + vatResults[1]))
                        .getBytes(StandardCharsets.UTF_8),
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
                String.format("Avans (beh):   %.2f", requestLastDocument.getPrepaymentSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Nisye:   %.2f", requestLastDocument.getCreditSum()).getBytes(StandardCharsets.UTF_8),
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

    private double[] calcVATPercentResult(List<CreateDocumentTransaction.VatAmount> vatData){

        double subjectToTax = 0.0, vatPercent = 0.0, nonSubjectToTax = 0.0;

        for (CreateDocumentTransaction.VatAmount data: vatData){

            if (data.getVatPercent() >= 0) {

                subjectToTax = data.getVatSum();
                vatPercent = data.getVatPercent();

            }else{
                nonSubjectToTax = data.getVatSum();
            }

            return new double[]{subjectToTax, vatPercent, nonSubjectToTax};
        }


        return null;
    }

    private byte[] iterateThroughItems(List<CreateDocumentTransaction.Item> items){
        if (items == null || items.isEmpty()) {
            System.out.println("NULL VALUE PRESENTED!!");
            return new byte[0];
        }

        System.out.println(123123);
        System.out.println("items size: " + items.size());
        System.out.println("items: " + items);
        for(CreateDocumentTransaction.Item item: items){
            byte[][] goodsStructure = new byte[][]{
                    "=========================================".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B,0x21,0x01},

                    // Left align
                    new byte[]{0x1B, 0x61, 0x00},
                    (item.getItemName().isEmpty() ? "-": item.getItemName()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},

                    //Tabulation
                    new byte[]{0x1B, 0x61, 0x00}, // Левое выравнивание (на всякий случай)

                    String.format("%.2f             %.2f             %.2f",
                            item.getItemQuantity(), item.getItemPrice(), item.getItemQuantity() * item.getItemPrice())
                            .getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("EDV %.2f" ,item.getItemVatPercent()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "=========================================".getBytes(StandardCharsets.UTF_8),
                    // Feed 2 lines
                    new byte[]{	0x1B, 0x64, 1}
            };

            byteList.add(toByteArray(concat(goodsStructure)));
        }
        return toByteArray(concat(byteList));
    }

    public void generate(){
        PrinterService printerService = new PrinterService();
        try {
            printerService.printReceipt(concat(moneyBackReceiptStructure));
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