package com.exam.android.kunj.api;

import com.exam.android.kunj.utils.Constant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
public class RestClient {

    public static Retrofit getInitializedRestAdapter() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.method(original.method(), original.body());
                Request request = requestBuilder.build();
                try {
                    return chain.proceed(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        httpClient.interceptors().add(interceptor);

        // add logging as last interceptor
        httpClient.interceptors().add(logging);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Constant.WEB_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create());
        return builder.build();

    }
}
