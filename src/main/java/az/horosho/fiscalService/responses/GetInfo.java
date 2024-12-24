package az.horosho.fiscalService.responses;
import java.io.*;
import java.io.Serializable;

public class GetInfo implements Serializable{
    private static final long serialVersionUID = 1L; 

    private String company_tax_number;
    private String company_name;
    private String object_tax_number;
    private String object_name;
    private String object_address;
    private String cashbox_tax_number;

    @Override
    public String toString() {
        return "GetInfo{" +
                "company_tax_number='" + company_tax_number + '\'' +
                ", company_name='" + company_name + '\'' +
                ", object_tax_number='" + object_tax_number + '\'' +
                ", object_name='" + object_name + '\'' +
                ", object_address='" + object_address + '\'' +
                ", cashbox_tax_number='" + cashbox_tax_number + '\'' +
                ", cashbox_factory_number='" + cashbox_factory_number + '\'' +
                ", firmware_version='" + firmware_version + '\'' +
                ", cashregister_factory_number='" + cashregister_factory_number + '\'' +
                ", cashregister_model='" + cashregister_model + '\'' +
                ", qr_code_url='" + qr_code_url + '\'' +
                ", not_before='" + not_before + '\'' +
                ", not_after='" + not_after + '\'' +
                ", state='" + state + '\'' +
                ", last_online_time='" + last_online_time + '\'' +
                ", oldest_document_time='" + oldest_document_time + '\'' +
                ", last_doc_number='" + last_doc_number + '\'' +
                ", production_uuid='" + production_uuid + '\'' +
                '}';
    }

    private String cashbox_factory_number;
    private String firmware_version;
    private String cashregister_factory_number;
    private String cashregister_model;
    private String qr_code_url;
    private String not_before;

    private String not_after;
    private String state;
    private String last_online_time;
    private String oldest_document_time;
    private String last_doc_number;
    private String production_uuid;

    public String getCompany_tax_number() {
        return company_tax_number;
    }

    public void setCompany_tax_number(String company_tax_number) {
        this.company_tax_number = company_tax_number;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getObject_tax_number() {
        return object_tax_number;
    }

    public void setObject_tax_number(String object_tax_number) {
        this.object_tax_number = object_tax_number;
    }

    public String getObject_name() {
        return object_name;
    }

    public void setObject_name(String object_name) {
        this.object_name = object_name;
    }

    public String getObject_address() {
        return object_address;
    }

    public void setObject_address(String object_address) {
        this.object_address = object_address;
    }

    public String getCashbox_tax_number() {
        return cashbox_tax_number;
    }

    public void setCashbox_tax_number(String cashbox_tax_number) {
        this.cashbox_tax_number = cashbox_tax_number;
    }

    public String getCashbox_factory_number() {
        return cashbox_factory_number;
    }

    public void setCashbox_factory_number(String cashbox_factory_number) {
        this.cashbox_factory_number = cashbox_factory_number;
    }

    public String getFirmware_version() {
        return firmware_version;
    }

    public void setFirmware_version(String firmware_version) {
        this.firmware_version = firmware_version;
    }

    public String getCashregister_factory_number() {
        return cashregister_factory_number;
    }

    public void setCashregister_factory_number(String cashregister_factory_number) {
        this.cashregister_factory_number = cashregister_factory_number;
    }

    public String getCashregister_model() {
        return cashregister_model;
    }

    public void setCashregister_model(String cashregister_model) {
        this.cashregister_model = cashregister_model;
    }

    public String getQr_code_url() {
        return qr_code_url;
    }

    public void setQr_code_url(String qr_code_url) {
        this.qr_code_url = qr_code_url;
    }

    public String getNot_before() {
        return not_before;
    }

    public void setNot_before(String not_before) {
        this.not_before = not_before;
    }

    public String getNot_after() {
        return not_after;
    }

    public void setNot_after(String not_after) {
        this.not_after = not_after;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLast_online_time() {
        return last_online_time;
    }

    public void setLast_online_time(String last_online_time) {
        this.last_online_time = last_online_time;
    }

    public String getOldest_document_time() {
        return oldest_document_time;
    }

    public void setOldest_document_time(String oldest_document_time) {
        this.oldest_document_time = oldest_document_time;
    }

    public String getLast_doc_number() {
        return last_doc_number;
    }

    public void setLast_doc_number(String last_doc_number) {
        this.last_doc_number = last_doc_number;
    }

    public String getProduction_uuid() {
        return production_uuid;
    }

    public void setProduction_uuid(String production_uuid) {
        this.production_uuid = production_uuid;
    }

    public static void serializeObject(GetInfo obj, String destPath){
        File file = new File(destPath);
        try(FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream ous = new ObjectOutputStream(fos)){
            if (!file.exists()) {
                file.createNewFile(); 
            }
            ous.writeObject(obj);
        }catch(IOException err){
            System.err.println("Error occurred!! Could not write a file!");
            err.printStackTrace();
        }
    }

    public static GetInfo deserializeObject(String pathToObj){
        File file = new File(pathToObj);

        if (!file.exists()) {
            System.err.println("File does not exist on specified path !!");
           return null;
        }

        try(FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis)){
            return (GetInfo)ois.readObject();
        }catch(IOException | ClassNotFoundException exc){
            System.err.println("Error occurred!! Could not read a file!");
            exc.printStackTrace();
        }

        return null;
    }


}
