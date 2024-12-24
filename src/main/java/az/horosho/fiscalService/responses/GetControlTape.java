package az.horosho.fiscalService.responses;

import java.util.List;

public class GetControlTape {
    private int documents_quantity;
    private int shiftNumber;
    private String shiftOpenAtUtc;
    private String shiftCloseAtUtc;
    private String createdAtUtc;

    public List<Tape> getTape() {
        return tape;
    }

    public void setTape(List<Tape> tape) {
        this.tape = tape;
    }

    private List<Tape> tape;

    public int getDocuments_quantity() {
        return documents_quantity;
    }

    public void setDocuments_quantity(int documents_quantity) {
        this.documents_quantity = documents_quantity;
    }

    public int getShiftNumber() {
        return shiftNumber;
    }

    public void setShiftNumber(int shiftNumber) {
        this.shiftNumber = shiftNumber;
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
}
