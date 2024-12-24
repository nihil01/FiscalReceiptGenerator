package az.horosho.fiscalService.responses;

import java.util.List;

public class Shift {

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

    private int reportsQuantity;


    // Getters and setters
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

        public String getDocumentId() {
            return document_id;
        }

        public void setDocumentId(String documentId) {
            this.document_id = documentId;
    }
}
