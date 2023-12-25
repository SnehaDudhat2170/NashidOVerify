package com.kyc.nashid.login.networking;

import com.kyc.nashid.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;

    private final Logger logger = Logger.withTag(this.getClass().getSimpleName());
    private static int timeOut=30;
    public static Retrofit getClient( String baseUrl, String token) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(token))
//                    .addInterceptor(new AuthInterceptor(getToken()))
                .build();

        retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrlFromJNI())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();


        return retrofit;
    }

}
