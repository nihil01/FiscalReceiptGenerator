package az.horosho.fiscalService.responses;

public class SaleResponse {
    private String document_id;
    private int document_number;
    private int shift_document_number;
    private String short_document_id;


    public void setDocument_id(String document_id){
        this.document_id = document_id;
    }

    public String getDocument_id(){
        return document_id;
    }

    public void setDocument_number(int document_number){
        this.document_number = document_number;
    }

    public int getDocument_number(){
        return document_number;
    }

    public void setShift_document_number(int shift_document_number){
        this.shift_document_number = shift_document_number;
    }

    public int getShift_document_number(){
        return shift_document_number;
    }

    public void setShort_document_id(String short_document_id){
        this.short_document_id = short_document_id;
    }

    public String getShort_document_id(){
        return short_document_id;
    }
}
