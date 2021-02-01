package com.codingcrew.memoir.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "https://memoir-api-v1.herokuapp.com/";
    public static RetrofitClient mInstance;
    public Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public APIClient getAPIClient() {
        return retrofit.create(APIClient.class);
    }
}
