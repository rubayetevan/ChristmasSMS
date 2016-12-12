package com.errorstation.christmassms;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Rubayet on 08-Dec-16.
 */

public interface SMSApi {

    String baseURL = "http://errorstation.com/smsapp/api/";
    @GET("getchristmassms.php?")
    Call<SMS> getFeaturedSMS(@Query("cat") String categoryID);

    @GET("smsview.php?")
    Call<SMS> reportView(@Query("smsid") String smsID);

    class Factory {
        public static SMSApi api;

        public static SMSApi getInstance() {
            if (api == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(baseURL)
                        .build();
                api = retrofit.create(SMSApi.class);
                return api;
            } else {
                return api;
            }
        }
    }
}
