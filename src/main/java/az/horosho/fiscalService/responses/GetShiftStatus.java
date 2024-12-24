package az.horosho.fiscalService.responses;

public class GetShiftStatus {
    private boolean shift_open;
    private String shift_open_time;

    public String getShift_open_time(){
        return shift_open_time;
    }

    public void setShift_open_time(String shift_open_time){
        this.shift_open_time = shift_open_time;
    }


    public boolean isShift_open() {
        return shift_open;
    }

    public void setShift_open(boolean shift_open) {
        this.shift_open = shift_open;
    }
}
