package com.exam.android.kunj.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

import com.exam.android.kunj.ImageAppApplication;
import com.exam.android.kunj.R;
import com.exam.android.kunj.db.models.ResultModel;
import com.exam.android.kunj.utils.DialogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        ButterKnife.bind(this);
        insertData();
    }

    @OnClick(R.id.home_screen_word_count)
    void onClickedWordCount() {
        ImageAppApplication application = (ImageAppApplication) getApplication();
        if(application != null && application.isNetConnected()) {
            Intent intent = new Intent(this, WebWordCountScreen.class);
            startActivity(intent);
        } else {
            DialogUtils.showNoConnectionDialog(this);
        }
    }

    @OnClick(R.id.home_screen_photo_gallery)
    void onClickedPhotoGallery() {
        Intent intent = new Intent(this, PhotoGalleryScreen.class);
        startActivity(intent);
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("raw.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void insertData() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                int itemCount = ImageAppApplication.database.imageDao().getItemCount();
                if(itemCount == 0) {
                    Gson gson = new GsonBuilder().create();
                    ResultModel resultModel = gson.fromJson(loadJSONFromAsset(), ResultModel.class);
                    ImageAppApplication.database.imageDao().insertAll(resultModel.getResults());
                }
                return null;
            }
        }.execute();
    }
}
