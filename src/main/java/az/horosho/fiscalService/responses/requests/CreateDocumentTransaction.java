package az.horosho.fiscalService.responses.requests;

import java.util.List;

public class CreateDocumentTransaction {

    private String access_token;
    private String doc_type;
    private Data data;

    // Getters and Setters
    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public String getDocType() {
        return doc_type;
    }

    public void setDocType(String docType) {
        this.doc_type = docType;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
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

        private int moneyBackType;
        private String parentDocument;
        private String cashier;
        private String currency;
        private String rrn;
        private String bonusCardNumber;
        private List<Item> items;
        private double sum;
        private double cashSum;
        private double cashlessSum;
        private double prepaymentSum;
        private double creditSum;
        private double bonusSum;
        private double incomingSum;
        private double changeSum;
        private List<VatAmount> vatAmounts;
        private String firstOperationAtUtc;
        private String lastOperationAtUtc;
        private List<String> parents;
        private int paymentNumber;
        private double residue;

        public int getPaymentNumber() {
            return paymentNumber;
        }

        public void setPaymentNumber(int paymentNumber) {
            this.paymentNumber = paymentNumber;
        }

        public double getResidue() {
            return residue;
        }

        public void setResidue(double residue) {
            this.residue = residue;
        }

        public String getCreditContract() {
            return creditContract;
        }

        public void setCreditContract(String creditContract) {
            this.creditContract = creditContract;
        }

        private String creditContract;

        public List<String> getParents() {
            return parents;
        }

        public void setParents(List<String> parents) {
            this.parents = parents;
        }

        public String getFirstOperationAtUtc() {
            return firstOperationAtUtc;
        }

        public void setFirstOperationAtUtc(String firstOperationAtUtc) {
            this.firstOperationAtUtc = firstOperationAtUtc;
        }

        public String getLastOperationAtUtc() {
            return lastOperationAtUtc;
        }

        public void setLastOperationAtUtc(String lastOperationAtUtc) {
            this.lastOperationAtUtc = lastOperationAtUtc;
        }

        // Getters and Setters
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

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
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

        public List<VatAmount> getVatAmounts() {
            return vatAmounts;
        }

        public void setVatAmounts(List<VatAmount> vatAmounts) {
            this.vatAmounts = vatAmounts;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "moneyBackType=" + moneyBackType +
                    ", parentDocument='" + parentDocument + '\'' +
                    ", cashier='" + cashier + '\'' +
                    ", currency='" + currency + '\'' +
                    ", rrn='" + rrn + '\'' +
                    ", bonusCardNumber='" + bonusCardNumber + '\'' +
                    ", items=" + items +
                    ", sum=" + sum +
                    ", cashSum=" + cashSum +
                    ", cashlessSum=" + cashlessSum +
                    ", prepaymentSum=" + prepaymentSum +
                    ", creditSum=" + creditSum +
                    ", bonusSum=" + bonusSum +
                    ", incomingSum=" + incomingSum +
                    ", changeSum=" + changeSum +
                    ", vatAmounts=" + vatAmounts +
                    ", firstOperationAtUtc='" + firstOperationAtUtc + '\'' +
                    ", lastOperationAtUtc='" + lastOperationAtUtc + '\'' +
                    ", parents=" + parents +
                    ", creditContract='" + creditContract + '\'' +
                    '}';
        }
    }

    public static class Item {

        private String itemName;
        private int itemCodeType;
        private String itemCode;
        private List<String> itemMarkingCodes;
        private int itemQuantityType;
        private double itemQuantity;
        private double itemPrice;
        private double itemSum;
        private double itemVatPercent;

        // Getters and Setters
        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getItemCodeType() {
            return itemCodeType;
        }

        public void setItemCodeType(int itemCodeType) {
            this.itemCodeType = itemCodeType;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public List<String> getItemMarkingCodes() {
            return itemMarkingCodes;
        }

        public void setItemMarkingCodes(List<String> itemMarkingCodes) {
            this.itemMarkingCodes = itemMarkingCodes;
        }

        public int getItemQuantityType() {
            return itemQuantityType;
        }

        public void setItemQuantityType(int itemQuantityType) {
            this.itemQuantityType = itemQuantityType;
        }

        public double getItemQuantity() {
            return itemQuantity;
        }

        public void setItemQuantity(double itemQuantity) {
            this.itemQuantity = itemQuantity;
        }

        public double getItemPrice() {
            return itemPrice;
        }

        public void setItemPrice(double itemPrice) {
            this.itemPrice = itemPrice;
        }

        public double getItemSum() {
            return itemSum;
        }

        public void setItemSum(double itemSum) {
            this.itemSum = itemSum;
        }

        public double getItemVatPercent() {
            return itemVatPercent;
        }

        public void setItemVatPercent(double itemVatPercent) {
            this.itemVatPercent = itemVatPercent;
        }
    }

    public static class VatAmount {

        private double vatSum;
        private double vatPercent;

        // Getters and Setters
        public double getVatSum() {
            return vatSum;
        }

        public void setVatSum(double vatSum) {
            this.vatSum = vatSum;
        }

        public double getVatPercent() {
            return vatPercent;
        }

        public void setVatPercent(double vatPercent) {
            this.vatPercent = vatPercent;
        }
    }

}
