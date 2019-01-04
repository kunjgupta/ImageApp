package com.exam.android.kunj.api.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
public interface WebContent {
    @GET(".")
    Call<ResponseBody> getWebContent();
}
