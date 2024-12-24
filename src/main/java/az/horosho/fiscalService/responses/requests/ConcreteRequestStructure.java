package az.horosho.fiscalService.responses.requests;

public class ConcreteRequestStructure<T> extends RequestStructure<T>{
    public ConcreteRequestStructure(T parameters, String operationId, int version){
        super(parameters, operationId, version);
    }
}
