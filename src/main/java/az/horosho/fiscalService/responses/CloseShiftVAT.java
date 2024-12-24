package az.horosho.fiscalService.responses;

public class CloseShiftVAT {
    private double vatSum;
    private double vatPercent;

    // Getters and setters
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
