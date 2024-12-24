package az.horosho.fiscalService.http;

public interface CommonCallback<T> {

    void onSuccess(T data);

    void onError(Throwable t);

} 
