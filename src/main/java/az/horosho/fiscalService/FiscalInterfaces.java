package az.horosho.fiscalService;

import az.horosho.fiscalService.responses.requests.ConcreteRequestStructure;
import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction;
import az.horosho.fiscalService.responses.*;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FiscalInterfaces {

    @POST("getInfo")
    Call<ConcreteResponseStructure<GetInfo>> getInfo();

    @POST("toLogin")
    Call<ConcreteResponseStructure<ToLogin>> getToLogin(@Body RequestBody inputData);

    @POST("toLogout")
    Call<ConcreteResponseStructure<ToLogin>> getToLogout(@Body RequestBody json);

    @POST("getShiftStatus")
    Call<ConcreteResponseStructure<GetShiftStatus>> getShiftStatus(@Body RequestBody json);

    @POST("openShift")
    Call<ConcreteResponseStructure<ToLogin>> openShift(@Body RequestBody json);

    @POST("closeShift")
    Call<ConcreteResponseStructure<Shift>> closeShift(@Body RequestBody json);

    @POST("createDocument")
    Call<ConcreteResponseStructure<CreateDocument>> createDocument(
        @Body ConcreteRequestStructure<CreateDocumentTransaction> json);

    @POST("getLastDocument")
    Call<ConcreteResponseStructure<GetLastDoc>> getLastDoc(@Body RequestBody json);

    @POST("getXReport")
    Call<ConcreteResponseStructure<Shift>> getXReport(@Body RequestBody json);

    @POST("getPeriodicZReport")
    Call<ConcreteResponseStructure<Shift>> getPeriodicZReport(@Body RequestBody json);

    @POST("getControlTape")
    Call<ConcreteResponseStructure<GetControlTape>> getControlTape(@Body RequestBody json);

}
