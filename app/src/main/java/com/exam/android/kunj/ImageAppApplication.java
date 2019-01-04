package com.exam.android.kunj;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.exam.android.kunj.db.AppDatabase;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
public class ImageAppApplication extends Application {

    public static AppDatabase database;
    private ConnectivityManager mConnectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getAppDatabase(this);
    }

    public boolean isNetConnected() {
        if (mConnectivityManager == null)
            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
