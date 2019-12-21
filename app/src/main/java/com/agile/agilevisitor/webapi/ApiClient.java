package com.agile.agilevisitor.webapi;

import android.content.Context;

import com.agile.agilevisitor.helper.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static OkHttpClient client;
    private static Retrofit retrofit = null;

    private static OkHttpClient clientOTP;
    private static Retrofit retrofitOTP = null;

    public static Retrofit getClient(Context context,int Flag) {
        if (retrofit == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);


            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new ConnectivityInterceptor(context))
                    .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(Utils.BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();


        }
        return retrofit;
    }

    public static Retrofit getOTPClient(Context context) {
        if (retrofitOTP == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);


            clientOTP = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new ConnectivityInterceptor(context))
                    .build();

            retrofitOTP = new Retrofit.Builder()
                    .baseUrl(Utils.BASE_OTP)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientOTP)
                    .build();

        }
        return retrofitOTP;
    }

}
