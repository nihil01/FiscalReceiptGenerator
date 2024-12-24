package az.horosho.fiscalService.responses;

import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction;

import java.util.List;

public class LastDocument {
    private String cashier;
    private String currency;
    private String rrn;
    private String bonusCardNumber;
    private List<CreateDocumentTransaction.Item> items;
    private double sum;
    private double cashSum;
    private double cashlessSum;
    private double prepaymentSum;
    private double creditSum;
    private double bonusSum;
    private double incomingSum;
    private double changeSum;
    private String firstOperationAtUtc;
    private String lastOperationAtUtc;
    private int moneyBackType;
    private String parentDocument;
    private int reportsQuantity;
    private String shiftOpenAtUtc;
    private String shiftCloseAtUtc;
    private String createdAtUtc;
    private int reportNumber;
    private int firstDocNumber;
    private int lastDocNumber;
    private int docCountToSend;
    private List<CloseShiftCurrencies> currencies;
    private String document_id;

    public int getReportsQuantity() {
        return reportsQuantity;
    }

    public void setReportsQuantity(int reportsQuantity) {
        this.reportsQuantity = reportsQuantity;
    }

    public String getShiftOpenAtUtc() {
        return shiftOpenAtUtc;
    }

    public void setShiftOpenAtUtc(String shiftOpenAtUtc) {
        this.shiftOpenAtUtc = shiftOpenAtUtc;
    }

    public String getShiftCloseAtUtc() {
        return shiftCloseAtUtc;
    }

    public void setShiftCloseAtUtc(String shiftCloseAtUtc) {
        this.shiftCloseAtUtc = shiftCloseAtUtc;
    }

    public String getCreatedAtUtc() {
        return createdAtUtc;
    }

    public void setCreatedAtUtc(String createdAtUtc) {
        this.createdAtUtc = createdAtUtc;
    }

    public int getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(int reportNumber) {
        this.reportNumber = reportNumber;
    }

    public int getFirstDocNumber() {
        return firstDocNumber;
    }

    public void setFirstDocNumber(int firstDocNumber) {
        this.firstDocNumber = firstDocNumber;
    }

    public int getLastDocNumber() {
        return lastDocNumber;
    }

    public void setLastDocNumber(int lastDocNumber) {
        this.lastDocNumber = lastDocNumber;
    }

    public int getDocCountToSend() {
        return docCountToSend;
    }

    public void setDocCountToSend(int docCountToSend) {
        this.docCountToSend = docCountToSend;
    }

    public List<CloseShiftCurrencies> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CloseShiftCurrencies> currencies) {
        this.currencies = currencies;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public int getMoneyBackType() {
        return moneyBackType;
    }

    public void setMoneyBackType(int moneyBackType) {
        this.moneyBackType = moneyBackType;
    }

    public String getParentDocument() {
        return parentDocument;
    }

    public void setParentDocument(String parentDocument) {
        this.parentDocument = parentDocument;
    }

    public String getLastOperationAtUtc() {
        return lastOperationAtUtc;
    }

    public void setLastOperationAtUtc(String lastOperationAtUtc) {
        this.lastOperationAtUtc = lastOperationAtUtc;
    }

    public String getFirstOperationAtUtc() {
        return firstOperationAtUtc;
    }

    public void setFirstOperationAtUtc(String firstOperationAtUtc) {
        this.firstOperationAtUtc = firstOperationAtUtc;
    }

    private List<CreateDocumentTransaction.VatAmount> vatAmounts;

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getBonusCardNumber() {
        return bonusCardNumber;
    }

    public void setBonusCardNumber(String bonusCardNumber) {
        this.bonusCardNumber = bonusCardNumber;
    }

    public List<CreateDocumentTransaction.Item> getItems() {
        return items;
    }

    public void setItems(List<CreateDocumentTransaction.Item> items) {
        this.items = items;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getCashSum() {
        return cashSum;
    }

    public void setCashSum(double cashSum) {
        this.cashSum = cashSum;
    }

    public double getCashlessSum() {
        return cashlessSum;
    }

    public void setCashlessSum(double cashlessSum) {
        this.cashlessSum = cashlessSum;
    }

    public double getPrepaymentSum() {
        return prepaymentSum;
    }

    public void setPrepaymentSum(double prepaymentSum) {
        this.prepaymentSum = prepaymentSum;
    }

    public double getCreditSum() {
        return creditSum;
    }

    public void setCreditSum(double creditSum) {
        this.creditSum = creditSum;
    }

    public double getBonusSum() {
        return bonusSum;
    }

    public void setBonusSum(double bonusSum) {
        this.bonusSum = bonusSum;
    }

    public double getIncomingSum() {
        return incomingSum;
    }

    public void setIncomingSum(double incomingSum) {
        this.incomingSum = incomingSum;
    }

    public double getChangeSum() {
        return changeSum;
    }

    public void setChangeSum(double changeSum) {
        this.changeSum = changeSum;
    }

    public List<CreateDocumentTransaction.VatAmount> getVatAmounts() {
        return vatAmounts;
    }

    public void setVatAmounts(List<CreateDocumentTransaction.VatAmount> vatAmounts) {
        this.vatAmounts = vatAmounts;
    }
}
