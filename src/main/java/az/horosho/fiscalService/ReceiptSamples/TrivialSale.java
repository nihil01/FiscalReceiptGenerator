package az.horosho.fiscalService.ReceiptSamples;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintException;

import az.horosho.fiscalService.ReceiptActions;
import az.horosho.fiscalService.responses.LastDocument;
import az.horosho.fiscalService.responses.requests.ConcreteRequestStructure;
import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction;
import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction.Data;
import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction.Item;
import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction.VatAmount;
import az.horosho.fiscalService.responses.ConcreteResponseStructure;
import az.horosho.fiscalService.responses.CreateDocument;
import az.horosho.fiscalService.responses.GetInfo;
import az.horosho.PrinterService;

public class TrivialSale implements Common{

    //original document
    private ConcreteRequestStructure<CreateDocumentTransaction> requestDocument;
    private ConcreteResponseStructure<CreateDocument> responseDocument;

    //copy of the document
    private String fiscalIDForLastDocument;
    private LastDocument requestLastDocument;

    //constants
    private final Type documentType;

    private byte[][] trivialSaleReceiptStructure;
    ArrayList<Byte[]> byteList = new ArrayList<>();

    public TrivialSale(ConcreteRequestStructure<CreateDocumentTransaction> requestDocument,
                       ConcreteResponseStructure<CreateDocument> responseDocument, Type documentType){
        this.requestDocument = requestDocument;
        this.responseDocument = responseDocument;
        this.documentType = documentType;
        initializeOriginalDocStructure();
    }

    public TrivialSale(LastDocument requestDocument,
                       String fiscalID, Type documentType){
        this.requestLastDocument = requestDocument;
        this.fiscalIDForLastDocument = fiscalID;
        this.documentType = documentType;
        initializeLastDocStructure();
    }

    public enum Type{ PREPAYMENT, CREDIT_PAY }

    private void initializeOriginalDocStructure(){
        GetInfo getInfoData = getInfoData();
        final String reportName = getString(requestDocument.getParameters().getData());

        nameExtractor(reportName);

        List<Item> items = requestDocument.getParameters().getData().getItems();
        for(Item item: items){

            byte[][] goodsStructure = new byte[][]{
            "=========================================".getBytes(StandardCharsets.UTF_8),
            new byte[]{0x0A},

            // Set font size to small (height and width reduced)
            new byte[]{0x1B,0x21,0x01},

            // Left align
            new byte[]{0x1B, 0x61, 0x00},
                item.getItemName().getBytes(StandardCharsets.UTF_8),
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

        System.out.println("FISCAL ID => " + responseDocument.getData().getDocument_id());
        Data calcVariables = requestDocument.getParameters().getData();
        double[] vatResults = calcVATPercentResult(calcVariables.getVatAmounts());

            byte[][] calculationBlock = new byte[][]{

                new byte[]{0x1B, 0x61, 0x00},
                new byte[]{0x0A},
                String.format("YEKUN MEBLEG:       %.2f", calcVariables.getSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("EDV-den AZAD:       %.2f", vatResults[2]).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("EDV %.2f :       %.2f",vatResults[1],vatResults[0]).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("YEKUN VERGI:       %.2f",
                (calcVariables.getSum() * vatResults[1])/(100 + vatResults[1]))
                .getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                "******************************************".getBytes(StandardCharsets.UTF_8),

               // Left align
                new byte[]{0x1B, 0x61, 0x00},
                "Odenis usulu:".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                ("Negdsiz: " + requestDocument.getParameters().getData().getCashlessSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                ("Negd: " + requestDocument.getParameters().getData().getCashSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Odenilib negd AZN:    %.2f", requestDocument.getParameters().getData().getIncomingSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Qaliq qaytarilib nagd AZN:   %.2f", requestDocument.getParameters().getData().getChangeSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Odenildi pul almadan(bonus karti):  %.2f", requestDocument.getParameters().getData().getBonusSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Avans (beh):   %.2f", requestDocument.getParameters().getData().getPrepaymentSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Nisye:   %.2f", requestDocument.getParameters().getData().getCreditSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                    concat(appendExtraDataForCreditPayment(requestDocument.getParameters().getData().getResidue(),
                            requestDocument.getParameters().getData().getPaymentNumber())),
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                // Feed 1 line
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

            byteList.add(toByteArray(concat(calculationBlock)));
    }

    private String getString(CreateDocumentTransaction.Data data) {
        System.out.println(data.toString());
        String reportName;

        if (data.getParents() != null &&
                !data.getParents().isEmpty()){
            reportName = "Satis (avans uzre) ceki";
        }
        else if(data.getCreditContract() != null &&
                !data.getCreditContract().isEmpty() && Type.CREDIT_PAY.equals(documentType)){
            reportName = "Kredit (nisye) odenisi";
        }
        else if(data.getCreditContract() != null &&
                !data.getCreditContract().isEmpty()){
            reportName = "Satis (nisye uzre) ceki";
        }
        else{
            reportName = Type.PREPAYMENT.equals(documentType) ? "Avans (beh) odenisi" : "Satis ceki";
        }
        return reportName;
    }

    private void initializeLastDocStructure(){
        var service = new PrinterService();
        GetInfo getInfoData = getInfoData();
        CreateDocumentTransaction.Data data = new CreateDocumentTransaction.Data();
        data.setCreditContract(requestLastDocument.getCreditContract());
        data.setParents(requestLastDocument.getParents());

        String reportName = getString(data);

        nameExtractor(reportName);

        List<Item> items = requestLastDocument.getItems();
        for(Item item: items){

            byte[][] goodsStructure = new byte[][]{
                    "=========================================".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B,0x21,0x01},

                    // Left align
                    new byte[]{0x1B, 0x61, 0x00},
                    "-".getBytes(StandardCharsets.UTF_8),
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


        double[] vatResults = calcVATPercentResult(requestLastDocument.getVatAmounts());

        byte[][] calculationBlock = new byte[][]{

                new byte[]{0x1B, 0x61, 0x00},
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

                // Left align
                new byte[]{0x1B, 0x61, 0x00},
                "Odenis usulu:".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                ("Negdsiz: " + requestLastDocument.getCashlessSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                ("Negd: " + requestLastDocument.getCashSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                String.format("Odenilib negd AZN:    %.2f", requestLastDocument.getIncomingSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Qaliq qaytarilib nagd AZN:    %.2f", requestLastDocument.getChangeSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Odenildi pul almadan(bonus karti):   %.2f", requestLastDocument.getBonusSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Avans (beh):   %.2f", requestLastDocument.getPrepaymentSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                String.format("Nisye:   %.2f", requestLastDocument.getCreditSum()).getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                concat(appendExtraDataForCreditPayment(requestLastDocument.getResidue(), requestLastDocument.getPaymentNumber())),
                "******************************************".getBytes(StandardCharsets.UTF_8),
                // Feed 1 line
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

        byteList.add(toByteArray(concat(calculationBlock)));


        try {
            service.printReceipt(toByteArray(concat(byteList)));
            ReceiptActions.generateFiscalQRCode(fiscalIDForLastDocument);
        } catch (PrintException e) {
            e.printStackTrace();
        }
    }

    private byte[][] prepayParentDocHeader(CreateDocumentTransaction.Data data, Type type){
        String toPaste;
        System.out.println("CHECK NOW TO PASTE");
        System.out.println(data);
        System.out.println(type);

        if (type == Type.PREPAYMENT){
            if (data.getParents() == null || data.getParents().isEmpty()){
                return new byte[][]{new byte[]{0}};
            }
            toPaste = "Avans(beh) odenis cekinin Fiscal ID-si: %s".formatted(data.getParents().getFirst());

        }else{
            if (data.getCreditContract() == null || data.getCreditContract().isEmpty()){
                return new byte[][]{new byte[]{0}};
            }
            toPaste = "Muqavilenin nomresi: %s".formatted(data.getCreditContract());
        }

        return new byte[][]{
                new byte[]{0x1B,0x21,0x01},
                new byte[]{0x0A},
                "******************************************".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                toPaste.getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                (data.getParentDocument() == null || data.getParentDocument().isEmpty()) ? "".getBytes() : ("Satis (nisye uzre) cekinin Fiscal ID-si: " + data.getParentDocument()).getBytes(),
                new byte[]{0x0A},
                "******************************************".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A}
        };
    }

    private byte[][] appendExtraDataForCreditPayment(double residue, int count){
        if (documentType != null && documentType.equals(Type.CREDIT_PAY)){
            return new byte[][]{
                    new byte[]{0x1B,0x21,0x01},
                    new byte[]{0x0A},
                    "******************************************".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Odenilecek qaliq mebleg (borc)    %.2f".formatted(residue).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    "Odenis uzre ardicilliq    %d".formatted(count).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A}
            };
        }
        return new byte[][]{new byte[]{0}};
    }

    private void nameExtractor(String reportName) {
        HeaderPart headerPart = generateHeaderPartCreateDocument(reportName, responseDocument);
        final Data data = getData();

        this.trivialSaleReceiptStructure = new byte[][]{
                concat(headerPart.getHeader()),
                concat(prepayParentDocHeader(data, documentType)),
                // Feed 2 lines
                new byte[]{	0x1B, 0x64, 1},

                // Set font size to small (height and width reduced)
                new byte[]{0x1B,0x21,0x01},

                // Left align
                new byte[]{0x1B, 0x61, 0x00},
                "Mehsulun adi".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},

                //Tabulation
                new byte[]{0x1B, 0x61, 0x00}, // Левое выравнивание (на всякий случай)
                "Miq.          Qiy.-AZN          Top.-AZN".getBytes(StandardCharsets.UTF_8),
                new byte[]{0x0A},
                // Feed 2 lines
                new byte[]{	0x1B, 0x64, 1}
        };

        byteList.add(toByteArray(concat(trivialSaleReceiptStructure)));
    }

    private Data getData() {
        Data data = new Data();

        if (responseDocument!= null && requestDocument.getParameters().getData() != null){

            data.setParents(requestDocument.getParameters().getData().getParents());
            data.setCreditContract(requestDocument.getParameters().getData().getCreditContract());
            data.setParentDocument(requestDocument.getParameters().getData().getParentDocument());

        }else{
            if (requestLastDocument != null){

                data.setParents(requestLastDocument.getParents());
                data.setCreditContract(requestLastDocument.getCreditContract());
                data.setParentDocument(requestLastDocument.getParentDocument());

            }
        }
        return data;
    }


    private double[] calcVATPercentResult(List<VatAmount> vatData){

        double subjectToTax = 0.0, vatPercent = 0.0, nonSubjectToTax = 0.0;

        for (VatAmount data: vatData){

            if (data.getVatPercent() >= 0) {

                subjectToTax = data.getVatSum();
                vatPercent = data.getVatPercent();

            }else{
                nonSubjectToTax = data.getVatSum();
            }

            double[] array = new double[]{subjectToTax, vatPercent, nonSubjectToTax};

            return array;
        }


        return null;
    }

    public void generate(){
        PrinterService printerService = new PrinterService();
        try {
            printerService.printReceipt(toByteArray(concat(byteList)));
            if (fiscalIDForLastDocument != null) {
                ReceiptActions.generateFiscalQRCode(fiscalIDForLastDocument);
                return;
            }
            ReceiptActions.generateFiscalQRCode(responseDocument.getData().getShort_document_id());
        } catch (PrintException e) {
            System.err.println("UNABLE TO PRINT Z REPORT RECEIPT");
            throw new RuntimeException(e);
        }

        concat(trivialSaleReceiptStructure);
    }
}
