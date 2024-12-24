package az.horosho.fiscalService.http;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HTTPProcessor {
    public <Req, Res> void process(Call<Res> call, CommonCallback<Res> cb) {
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Res responseBody = response.body();                  
                    cb.onSuccess(responseBody);
                } else {
                    System.out.println("Unsuccessful response. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Res> call, Throwable t) {
                cb.onError(t);
            }
        });
    }
}
