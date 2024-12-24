package az.horosho.fiscalService.ReceiptSamples;

import az.horosho.ConfigAttributes;
import az.horosho.PrinterHelperMethods;
import az.horosho.PrinterService;
import az.horosho.fiscalService.ReceiptActions;
import az.horosho.fiscalService.responses.*;

import javax.print.PrintException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ZReport implements Common{
    private ConcreteResponseStructure<Shift> response;
    private LastDocument responseLastDoc;
    private byte[][] ZReportStructure;

    private double commonTaxes = 0;
    private double commonSum = 0;
    private final Type type;
    private String fiscalID;

    public enum Type{ X, Z, PERIODIC_Z }

    public ZReport(ConcreteResponseStructure<Shift> response, Type type) {
        this.response = response;
        this.type = type;
        initializeOriginalStructure();
    }

    public ZReport(LastDocument responseLastDoc, String fiscalID, Type type) {
        this.responseLastDoc = responseLastDoc;
        this.type = type;
        this.fiscalID = fiscalID;
        initializeLastDocStructure();
    }

    private void initializeOriginalStructure(){
        PrinterHelperMethods printerHelperMethods = new PrinterHelperMethods();
        String path = printerHelperMethods.getDataFromConfig(printerHelperMethods.CONFIG_PATH,
                ConfigAttributes.GET_INFO_OBJ_PATH);
        GetInfo getInfoData = GetInfo.deserializeObject(path);

        String reportName;
        reportName = this.type == ZReport.Type.X ? "X Hesabati" : this.type == ZReport.Type.PERIODIC_Z? "Z-Hesabat(dovurlu)" : "Z Hesabati";

        HeaderPart headerPart = new HeaderPart(getInfoData, reportName, response.getData().getReportNumber());

        if (getInfoData == null){
            System.err.println("getInfoData is null. Aborting..");
            return;
        }

        System.out.println("RESPONSE ARRAY HERE");

        System.out.println(response.getData().getCurrencies());
        calcAllTaxesOnce(response.getData().getCurrencies());

        if (!response.getData().getCurrencies().isEmpty()) {
            CloseShiftCurrencies closeShiftCurrencies = response.getData().getCurrencies().getFirst();
            this.ZReportStructure = new byte[][]{
                    concat(headerPart.getHeader()),
                    new byte[]{	0x1B, 0x64, 1},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B,0x21,0x01},

                    "******************************************".getBytes(),
                    new byte[]{0x0A},
                    ("Novbenin acilma vaxti: " + response.getData().getShiftOpenAtUtc()).getBytes(),
                    new byte[]{0x0A},
                    ("Hesabatin alinma vaxti: " + response.getData().getCreatedAtUtc()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(),
                    new byte[]{0x0A},
                    generateZPeriodicHeader(response.getData().getShiftOpenAtUtc(), response.getData().getShiftCloseAtUtc()),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "Kassa cekleri:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Birinci kassa cekinin nomresi:               %d".formatted(response.getData().getFirstDocNumber()).getBytes(),
                    new byte[]{0x0A},
                    "Sonuncu kassa cekinin nomresi:               %d".formatted(response.getData().getLastDocNumber()).getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x0A},
                    "Dovriyye:               %.2f".formatted(commonSum).getBytes(),
                    new byte[]{0x0A},
                    "Dovriyye uzre vergi:               %.2f".formatted(commonTaxes).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "SATIS:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Satis ceklerinin sayi:      %d".formatted(closeShiftCurrencies.getSaleCount()).getBytes(),
                    new byte[]{0x0A},
                    "Satis mebleginin cemi:      %.2f".formatted(closeShiftCurrencies.getSaleSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getSaleCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getSaleCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getSaleBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(closeShiftCurrencies.getPrepaySum()).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(closeShiftCurrencies.getCreditpaySum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getSaleVatAmounts())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "GERI QAYTARMA:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Geri qaytarma ceklerinin sayi:   %d".formatted(closeShiftCurrencies.getMoneyBackCount()).getBytes(),
                    new byte[]{0x0A},
                    "Geri qaytarma mebleginin cemi:   %.2f".formatted(closeShiftCurrencies.getMoneyBackSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getMoneyBackCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getMoneyBackCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getMoneyBackBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(closeShiftCurrencies.getMoneyBackPrepaymentSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(closeShiftCurrencies.getMoneyBackCreditSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getMoneyBackVatAmounts())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "LEGV ETME:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Legv etme ceklerinin sayi:   %d".formatted(closeShiftCurrencies.getRollbackCount()).getBytes(),
                    new byte[]{0x0A},
                    "Legv etme mebleginin cemi:   %.2f".formatted(closeShiftCurrencies.getRollbackSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getRollbackCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getRollbackCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getRollbackBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(closeShiftCurrencies.getRollbackPrepaymentSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(closeShiftCurrencies.getRollbackCreditSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getRollbackVatAmounts())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "MEDAXIL:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Medaxil ceklerinin sayi: %d".formatted(closeShiftCurrencies.getDepositCount()).getBytes(),
                    new byte[]{0x0A},
                    "Medaxil mebleginin cemi: %.2f".formatted(closeShiftCurrencies.getDepositSum()).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "MEXARIC:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Mexaric ceklerinin sayi: %d".formatted(closeShiftCurrencies.getWithdrawCount()).getBytes(),
                    new byte[]{0x0A},
                    "Mexaric mebleginin cemi: %.2f".formatted(closeShiftCurrencies.getWithdrawSum()).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "KORREKSIYA:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Korreksiya ceklerinin sayi:   %d".formatted(closeShiftCurrencies.getCorrectionCount()).getBytes(),
                    new byte[]{0x0A},
                    "Korreksiya mebleginin cemi:   %.2f".formatted(closeShiftCurrencies.getCorrectionSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getCorrectionCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getCorrectionCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getCorrectionBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getCorrectionCloseShiftVATs())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "AVANS(BEH) ODENISI:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Avans(beh) odenisi ceklerinin sayi:   %d".formatted(closeShiftCurrencies.getPrepayCount()).getBytes(),
                    new byte[]{0x0A},
                    "Avans(beh) odenisi mebleginin cemi:   %.2f".formatted(closeShiftCurrencies.getPrepaySum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getPrepayCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getPrepayCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getPrepayBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getPrepayVatAmounts())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "KREDIT(NISYE) ODENISI:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Kredit(nisye) odenisi ceklerinin sayi: %d".formatted(closeShiftCurrencies.getCreditpayCount()).getBytes(),
                    new byte[]{0x0A},
                    "Kredit(nisye) odenisi mebleginin cemi: %.2f".formatted(closeShiftCurrencies.getCreditpaySum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getCreditpayCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getCreditpayCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getCreditpayBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getCreditpayVatAmounts())).getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x0A},
                    "******************************************".getBytes(),
// Feed 1 line
                    new byte[]{0x0A},
                    String.format("Novbe erzinde vurulmus cek sayi: %d", 1 + response.getData().getLastDocNumber() - response.getData().getFirstDocNumber()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("DVX-ye gonderilmeyen ceklerin sayi: %d", response.getData().getDocCountToSend()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Kassa aparatin modeli: %s", getInfoData.getCashregister_model()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Kassa aparatin seriya nomresi: %s", getInfoData.getCashbox_factory_number()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Fiscal ID: %s", response.getData().getDocumentId()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("NMQ-nun qeydiyyat nomresi: %s", getInfoData.getCashbox_tax_number()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x01},
                    "www.e-kassa.az".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A}
            };
        }else{
            this.ZReportStructure = new byte[][] {
                    concat(headerPart.getHeader()),
                    new byte[]{	0x1B, 0x64, 1},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B,0x21,0x01},

                    "******************************************".getBytes(),
                    new byte[]{0x0A},
                    ("Novbenin acilma vaxti: " + response.getData().getShiftOpenAtUtc()).getBytes(),
                    new byte[]{0x0A},
                    ("Hesabatin alinma vaxti: " + response.getData().getCreatedAtUtc()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(),
                    new byte[]{0x0A},
                    generateZPeriodicHeader(response.getData().getShiftOpenAtUtc(), response.getData().getShiftCloseAtUtc()),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "Kassa cekleri:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Birinci kassa cekinin nomresi:               %d".formatted(response.getData().getFirstDocNumber()).getBytes(),
                    new byte[]{0x0A},
                    "Sonuncu kassa cekinin nomresi:               %d".formatted(response.getData().getLastDocNumber()).getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x0A},
                    "Dovriyye:               %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Dovriyye uzre vergi:               %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "SATIS:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Satis ceklerinin sayi:      %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Satis mebleginin cemi:      %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "GERI QAYTARMA:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Geri qaytarma ceklerinin sayi:   %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Geri qaytarma mebleginin cemi:   %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "LEGV ETME:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Legv etme ceklerinin sayi:   %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Legv etme mebleginin cemi:   %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "MEDAXIL:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Medaxil ceklerinin sayi: %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Medaxil mebleginin cemi: %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "MEXARIC:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Mexaric ceklerinin sayi: %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Mexaric mebleginin cemi: %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "KORREKSIYA:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Korreksiya ceklerinin sayi:   %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Korreksiya mebleginin cemi:   %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "AVANS(BEH) ODENISI:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Avans(beh) odenisi ceklerinin sayi:   %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Avans(beh) odenisi mebleginin cemi:   %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "KREDIT(NISYE) ODENISI:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Kredit(nisye) odenisi ceklerinin sayi: %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Kredit(nisye) odenisi mebleginin cemi: %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(),
// Feed 1 line
                    new byte[]{0x0A},
                    String.format("Novbe erzinde vurulmus cek sayi: %d", 1+ response.getData().getLastDocNumber() - response.getData().getFirstDocNumber()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("DVX-ye gonderilmeyen ceklerin sayi: %d", response.getData().getDocCountToSend()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Kassa aparatin modeli: %s", getInfoData.getCashregister_model()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Kassa aparatin seriya nomresi: %s", getInfoData.getCashbox_factory_number()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Fiscal ID: %s", response.getData().getDocumentId()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("NMQ-nun qeydiyyat nomresi: %s", getInfoData.getCashbox_tax_number()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x01},
                    "www.e-kassa.az".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A}
            };
        }
    }
    
    private void initializeLastDocStructure(){
        PrinterHelperMethods printerHelperMethods = new PrinterHelperMethods();
        String path = printerHelperMethods.getDataFromConfig(printerHelperMethods.CONFIG_PATH,
                ConfigAttributes.GET_INFO_OBJ_PATH);
        GetInfo getInfoData = GetInfo.deserializeObject(path);

        String reportName;
        reportName =  this.type == Type.PERIODIC_Z? "Z-Hesabat(dovurlu)(Get Last Document)" : "Z Hesabati(Get Last Document)";

        HeaderPart headerPart = new HeaderPart(getInfoData, reportName, responseLastDoc.getReportNumber());

        if (getInfoData == null){
            System.err.println("getInfoData is null. Aborting..");
            return;
        }

        System.out.println("RESPONSE ARRAY HERE");

        System.out.println(responseLastDoc.getCurrencies());
        calcAllTaxesOnce(responseLastDoc.getCurrencies());

        if (!responseLastDoc.getCurrencies().isEmpty()) {
            CloseShiftCurrencies closeShiftCurrencies = responseLastDoc.getCurrencies().getFirst();
            this.ZReportStructure = new byte[][]{
                    concat(headerPart.getHeader()),
                    new byte[]{	0x1B, 0x64, 1},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B,0x21,0x01},

                    "******************************************".getBytes(),
                    new byte[]{0x0A},
                    ("Novbenin acilma vaxti: " + responseLastDoc.getShiftOpenAtUtc()).getBytes(),
                    new byte[]{0x0A},
                    ("Hesabatin alinma vaxti: " + responseLastDoc.getCreatedAtUtc()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(),
                    new byte[]{0x0A},
                    generateZPeriodicHeader(responseLastDoc.getShiftOpenAtUtc(), responseLastDoc.getShiftCloseAtUtc()),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "Kassa cekleri:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Birinci kassa cekinin nomresi:               %d".formatted(responseLastDoc.getFirstDocNumber()).getBytes(),
                    new byte[]{0x0A},
                    "Sonuncu kassa cekinin nomresi:               %d".formatted(responseLastDoc.getLastDocNumber()).getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x0A},
                    "Dovriyye:               %.2f".formatted(commonSum).getBytes(),
                    new byte[]{0x0A},
                    "Dovriyye uzre vergi:               %.2f".formatted(commonTaxes).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "SATIS:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Satis ceklerinin sayi:      %d".formatted(closeShiftCurrencies.getSaleCount()).getBytes(),
                    new byte[]{0x0A},
                    "Satis mebleginin cemi:      %.2f".formatted(closeShiftCurrencies.getSaleSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getSaleCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getSaleCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getSaleBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(closeShiftCurrencies.getPrepaySum()).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(closeShiftCurrencies.getCreditpaySum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getSaleVatAmounts())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "GERI QAYTARMA:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Geri qaytarma ceklerinin sayi:   %d".formatted(closeShiftCurrencies.getMoneyBackCount()).getBytes(),
                    new byte[]{0x0A},
                    "Geri qaytarma mebleginin cemi:   %.2f".formatted(closeShiftCurrencies.getMoneyBackSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getMoneyBackCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getMoneyBackCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getMoneyBackBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(closeShiftCurrencies.getMoneyBackPrepaymentSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(closeShiftCurrencies.getMoneyBackCreditSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getMoneyBackVatAmounts())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "LEGV ETME:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Legv etme ceklerinin sayi:   %d".formatted(closeShiftCurrencies.getRollbackCount()).getBytes(),
                    new byte[]{0x0A},
                    "Legv etme mebleginin cemi:   %.2f".formatted(closeShiftCurrencies.getRollbackSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getRollbackCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getRollbackCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getRollbackBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(closeShiftCurrencies.getRollbackPrepaymentSum()).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(closeShiftCurrencies.getRollbackCreditSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getRollbackVatAmounts())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "MEDAXIL:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Medaxil ceklerinin sayi: %d".formatted(closeShiftCurrencies.getDepositCount()).getBytes(),
                    new byte[]{0x0A},
                    "Medaxil mebleginin cemi: %.2f".formatted(closeShiftCurrencies.getDepositSum()).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "MEXARIC:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Mexaric ceklerinin sayi: %d".formatted(closeShiftCurrencies.getWithdrawCount()).getBytes(),
                    new byte[]{0x0A},
                    "Mexaric mebleginin cemi: %.2f".formatted(closeShiftCurrencies.getWithdrawSum()).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "KORREKSIYA:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Korreksiya ceklerinin sayi:   %d".formatted(closeShiftCurrencies.getCorrectionCount()).getBytes(),
                    new byte[]{0x0A},
                    "Korreksiya mebleginin cemi:   %.2f".formatted(closeShiftCurrencies.getCorrectionSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getCorrectionCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getCorrectionCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getCorrectionBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getCorrectionCloseShiftVATs())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "AVANS(BEH) ODENISI:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Avans(beh) odenisi ceklerinin sayi:   %d".formatted(closeShiftCurrencies.getPrepayCount()).getBytes(),
                    new byte[]{0x0A},
                    "Avans(beh) odenisi mebleginin cemi:   %.2f".formatted(closeShiftCurrencies.getPrepaySum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getPrepayCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getPrepayCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getPrepayBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getPrepayVatAmounts())).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "KREDIT(NISYE) ODENISI:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Kredit(nisye) odenisi ceklerinin sayi: %d".formatted(closeShiftCurrencies.getCreditpayCount()).getBytes(),
                    new byte[]{0x0A},
                    "Kredit(nisye) odenisi mebleginin cemi: %.2f".formatted(closeShiftCurrencies.getCreditpaySum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(closeShiftCurrencies.getCreditpayCashSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(closeShiftCurrencies.getCreditpayCashlessSum()).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(closeShiftCurrencies.getCreditpayBonusSum()).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(calcTaxes(closeShiftCurrencies.getCreditpayVatAmounts())).getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x0A},
                    "******************************************".getBytes(),
                    // Feed 1 line
                    new byte[]{0x0A},
                    String.format("Novbe erzinde vurulmus cek sayi: %d", 1 + responseLastDoc.getLastDocNumber() - responseLastDoc.getFirstDocNumber()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("DVX-ye gonderilmeyen ceklerin sayi: %d", responseLastDoc.getDocCountToSend()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Kassa aparatin modeli: %s", getInfoData.getCashregister_model()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Kassa aparatin seriya nomresi: %s", getInfoData.getCashbox_factory_number()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Fiscal ID: %s", fiscalID).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("NMQ-nun qeydiyyat nomresi: %s", getInfoData.getCashbox_tax_number()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x01},
                    "www.e-kassa.az".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A}
            };
        }else{
            this.ZReportStructure = new byte[][] {
                    concat(headerPart.getHeader()),
                    new byte[]{	0x1B, 0x64, 1},

                    // Set font size to small (height and width reduced)
                    new byte[]{0x1B,0x21,0x01},

                    "******************************************".getBytes(),
                    new byte[]{0x0A},
                    ("Novbenin acilma vaxti: " + responseLastDoc.getShiftOpenAtUtc()).getBytes(),
                    new byte[]{0x0A},
                    ("Hesabatin alinma vaxti: " + responseLastDoc.getCreatedAtUtc()).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(),
                    new byte[]{0x0A},
                    generateZPeriodicHeader(responseLastDoc.getShiftOpenAtUtc(), responseLastDoc.getShiftCloseAtUtc()),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "Kassa cekleri:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Birinci kassa cekinin nomresi:               %d".formatted(responseLastDoc.getFirstDocNumber()).getBytes(),
                    new byte[]{0x0A},
                    "Sonuncu kassa cekinin nomresi:               %d".formatted(responseLastDoc.getLastDocNumber()).getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x0A},
                    "Dovriyye:               %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Dovriyye uzre vergi:               %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "SATIS:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Satis ceklerinin sayi:      %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Satis mebleginin cemi:      %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "GERI QAYTARMA:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Geri qaytarma ceklerinin sayi:   %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Geri qaytarma mebleginin cemi:   %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "LEGV ETME:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Legv etme ceklerinin sayi:   %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Legv etme mebleginin cemi:   %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Avans(beh) meblegi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "*Nisye (kredit) alinmis mebleg:      %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "MEDAXIL:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Medaxil ceklerinin sayi: %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Medaxil mebleginin cemi: %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "MEXARIC:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Mexaric ceklerinin sayi: %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Mexaric mebleginin cemi: %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "KORREKSIYA:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Korreksiya ceklerinin sayi:   %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Korreksiya mebleginin cemi:   %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "AVANS(BEH) ODENISI:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Avans(beh) odenisi ceklerinin sayi:   %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Avans(beh) odenisi mebleginin cemi:   %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},

                    new byte[]{0x1B, 0x61, 0x01},
                    "KREDIT(NISYE) ODENISI:".getBytes(),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x00},
                    "Kredit(nisye) odenisi ceklerinin sayi: %d".formatted(0).getBytes(),
                    new byte[]{0x0A},
                    "Kredit(nisye) odenisi mebleginin cemi: %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagd:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Nagdsiz:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "  -Bonus:        %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "Vergi mebleginin cemi:       %.2f".formatted(0.0).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes(),
                    // Feed 1 line
                    new byte[]{0x0A},
                    String.format("Novbe erzinde vurulmus cek sayi: %d", 1 + responseLastDoc.getLastDocNumber() - responseLastDoc.getFirstDocNumber() + 1).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("DVX-ye gonderilmeyen ceklerin sayi: %d", responseLastDoc.getDocCountToSend()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Kassa aparatin modeli: %s", getInfoData.getCashregister_model()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Kassa aparatin seriya nomresi: %s", getInfoData.getCashbox_factory_number()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("Fiscal ID: %s", fiscalID).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    String.format("NMQ-nun qeydiyyat nomresi: %s", getInfoData.getCashbox_tax_number()).getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A},
                    new byte[]{0x1B, 0x61, 0x01},
                    "www.e-kassa.az".getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x0A}
            };
        }
    }

    public double calcTaxes(List<CloseShiftVAT> dataList) {
        System.out.println(dataList);
        double VAT18 = 0, VAT_NON_18 = 0;
        for (CloseShiftVAT data : dataList) {
           if (data.getVatPercent() == 18){
               VAT18 = (data.getVatPercent() * data.getVatSum())/ (100 + data.getVatPercent());
           }else{
               VAT_NON_18 = (data.getVatPercent() * data.getVatSum())/ (100 + data.getVatPercent());
           }
        }
        System.out.println("VAT 18 " + VAT18);
        System.out.println("VAT NON_18 " + VAT_NON_18);

        return VAT18 + VAT_NON_18;
    }

    public void calcAllTaxesOnce(List<CloseShiftCurrencies> dataList) {

        if (dataList.isEmpty()){
            return;
        }

        CloseShiftCurrencies closeShiftCurrencies = dataList.getFirst();

        List<CloseShiftVAT> correctionVAT = closeShiftCurrencies.getCorrectionCloseShiftVATs();
        List<CloseShiftVAT> salesVAT = closeShiftCurrencies.getSaleVatAmounts();
        List<CloseShiftVAT> creditpayVAT = closeShiftCurrencies.getCreditpayVatAmounts();
        List<CloseShiftVAT> rollbackVAT = closeShiftCurrencies.getRollbackVatAmounts();
        List<CloseShiftVAT> moneyBackVAT = closeShiftCurrencies.getMoneyBackVatAmounts();
        List<CloseShiftVAT> prepayVAT = closeShiftCurrencies.getPrepayVatAmounts();

        getVatAttributes(correctionVAT, salesVAT, creditpayVAT, rollbackVAT, moneyBackVAT, prepayVAT);
    }

    private void getVatAttributes(List<CloseShiftVAT> correctionVAT, List<CloseShiftVAT> salesVAT, List<CloseShiftVAT> creditpayVAT,
                                  List<CloseShiftVAT> rollbackVAT, List<CloseShiftVAT> moneyBackVAT, List<CloseShiftVAT> prepayVAT) {
        for (CloseShiftVAT data : correctionVAT) {
            commonSum += data.getVatSum();
            commonTaxes += (data.getVatSum() * data.getVatPercent()) / (100 + data.getVatPercent());
        }

        for (CloseShiftVAT data : salesVAT) {
            commonSum += data.getVatSum();
            commonTaxes += (data.getVatSum() * data.getVatPercent()) / (100 + data.getVatPercent());
        }

        for (CloseShiftVAT data : creditpayVAT) {
            commonSum += data.getVatSum();
            commonTaxes += (data.getVatSum() * data.getVatPercent()) / (100 + data.getVatPercent());
        }

        for (CloseShiftVAT data : rollbackVAT) {
            commonSum -= data.getVatSum();
            commonTaxes -= (data.getVatSum() * data.getVatPercent()) / (100 + data.getVatPercent());
        }

        for (CloseShiftVAT data : moneyBackVAT) {
            commonSum -= data.getVatSum();
            commonTaxes -= (data.getVatSum() * data.getVatPercent()) / (100 + data.getVatPercent());
        }

        for (CloseShiftVAT data : prepayVAT) {
            commonSum += data.getVatSum();
            commonTaxes += (data.getVatSum() * data.getVatPercent()) / (100 + data.getVatPercent());
        }
    }

    public byte[] generateZPeriodicHeader(String shiftOpen, String shiftClose) {
        if (this.type == Type.PERIODIC_Z){
            return concat(new byte[][]{
                    "Hesabat dovru: %s - %s".formatted(shiftOpen,
                            shiftClose).getBytes(),
                    new byte[]{0x0A},
                    "******************************************".getBytes()
            });
        }
        return new byte[0];
    }

    public void generate(){
        PrinterService printerService = new PrinterService();
        try {
            printerService.printReceipt(concat(ZReportStructure));
            if (this.type == Type.Z){
                if (fiscalID != null){
                    ReceiptActions.generateFiscalQRCode(fiscalID);
                    return;
                }
                ReceiptActions.generateFiscalQRCode(response.getData().getDocumentId());
            }
        } catch (PrintException e) {
            System.err.println("UNABLE TO PRINT Z REPORT RECEIPT");
            throw new RuntimeException(e);
        }
    }


}
