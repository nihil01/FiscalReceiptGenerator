package az.horosho.fiscalService.responses;

public class ConcreteResponseStructure<T> extends ResponseStructure<T> {

    public ConcreteResponseStructure(String message, int code, T data){
        super(code, message, data);
    }
        
}
