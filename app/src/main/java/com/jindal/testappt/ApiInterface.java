package com.jindal.testappt;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("index.php")
    @FormUrlEncoded
    Call<DataPojo> getApiData(@Field("API") String api,
                                    @Field("serviceid") String serviceid);

}
