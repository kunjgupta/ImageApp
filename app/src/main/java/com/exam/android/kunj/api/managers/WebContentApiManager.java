package com.exam.android.kunj.api.managers;

import com.exam.android.kunj.api.RestClient;
import com.exam.android.kunj.api.interfaces.WebContent;
import com.exam.android.kunj.api.listeners.get.GetWebContentListener;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
public class WebContentApiManager {
    public static void getWebContent(GetWebContentListener listener) {
        final Retrofit retrofit = RestClient.getInitializedRestAdapter();
        Call<ResponseBody> call = retrofit.create(WebContent.class).getWebContent();
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                listener.onDoneApiCall(response.body().string());
            } else {
                listener.onFailApiCall();
            }
        } catch (IOException e) {
            listener.onFailApiCall();
        }
    }
}
