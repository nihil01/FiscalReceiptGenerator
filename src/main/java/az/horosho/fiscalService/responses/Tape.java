package az.horosho.fiscalService.responses;

public class    Tape {
    private String type;
    private int number;
    private String createdAtUtc;
    private int item_count;
    private double total_sum;
    private double total_vat;
    private String document_id;
    private String short_document_id;
    private boolean delivered;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getItem_count() {
        return item_count;
    }

    public void setItem_count(int item_count) {
        this.item_count = item_count;
    }

    public double getTotal_sum() {
        return total_sum;
    }

    public void setTotal_sum(double total_sum) {
        this.total_sum = total_sum;
    }

    public double getTotal_vat() {
        return total_vat;
    }

    public void setTotal_vat(double total_vat) {
        this.total_vat = total_vat;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getShort_document_id() {
        return short_document_id;
    }

    public void setShort_document_id(String short_document_id) {
        this.short_document_id = short_document_id;
    }
// Getters and Setters


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getCreatedAtUtc() {
        return createdAtUtc;
    }

    public void setCreatedAtUtc(String createdAtUtc) {
        this.createdAtUtc = createdAtUtc;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
}

