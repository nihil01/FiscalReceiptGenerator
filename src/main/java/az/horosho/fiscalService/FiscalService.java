package az.horosho.fiscalService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FiscalService {
    public static FiscalInterfaces getFiscalService() {

        Gson gson = new GsonBuilder()
                    .setLenient() 
                    .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:6768/api/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(FiscalInterfaces.class);
    }
}
