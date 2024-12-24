package az.horosho.fiscalService.responses.requests;

public class RequestStructure<T> {
    private T parameters;
    private String operationId;
    private int version;

    public RequestStructure(T parameters, String operationId, int version){
        this.parameters = parameters;
        this.operationId = operationId;
        this.version = version;
    }

    public T getParameters(){
        return parameters;
    }

    public void setParameters(T parameters){
        this.parameters = parameters;
    }

    public void setOperationId(String operationId){
        this.operationId = operationId;
    }

    public String getOperationId(){
        return operationId;
    }

    public void setVersion(int version){
        this.version = version;
    }

    public int getVersion(){
        return version;
    }
}
