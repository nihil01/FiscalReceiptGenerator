package az.horosho.fiscalService.responses;

public class GetLastDoc {
    private String document_id;
    private String doc_type;
    private LastDocument doc;

    public String document_id() {
        return document_id;
    }

    public String doc_type() {
        return doc_type;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }


    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

    public LastDocument doc() {
        return doc;
    }

    public void setDoc(LastDocument doc) {
        this.doc = doc;
    }
}
