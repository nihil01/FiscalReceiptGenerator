package az.horosho.fiscalService.responses;

public class GetPeriodicZReport {
    private Shift data;
    private int reportsQuantity;
    private String createdAtUtc;


    public Shift getData() {
        return data;
    }

    public void setData(Shift data) {
        this.data = data;
    }

    public int getReportsQuantity() {
        return reportsQuantity;
    }

    public void setReportsQuantity(int reportsQuantity) {
        this.reportsQuantity = reportsQuantity;
    }

    public String getCreatedAtUtc() {
        return createdAtUtc;
    }

    public void setCreatedAtUtc(String createdAtUtc) {
        this.createdAtUtc = createdAtUtc;
    }
}
