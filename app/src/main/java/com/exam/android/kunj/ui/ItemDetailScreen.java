package com.exam.android.kunj.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.exam.android.kunj.ImageAppApplication;
import com.exam.android.kunj.R;
import com.exam.android.kunj.db.models.ImageModel;
import com.exam.android.kunj.utils.Constant;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */

public class ItemDetailScreen extends AppCompatActivity {

    @BindView(R.id.item_detail_screen_image_view)
    ImageView mImageView;
    @BindView(R.id.item_detail_screen_title_text_view)
    TextView mTitleTextView;
    @BindView(R.id.item_detail_screen_description_text_view)
    TextView mDescriptionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_detail_screen);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
            actionBar.setTitle(R.string.item_detail_screen_action_bar_title);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle == null) {
            Log.e("ItemDetailScreen", "onCreate: bundle is null");
            finish();
            return;
        }
        int id = bundle.getInt(Constant.IMAGE_MODEL_ID);
        if(id == -1) {
            Log.e("ItemDetailScreen", "onCreate: Image Model ID is null");
            finish();
            return;
        }

        fetchData(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void fetchData(final int id) {
        new AsyncTask<Void, Void, ImageModel>() {

            @Override
            protected ImageModel doInBackground(Void... voids) {
                ImageModel imageModel = ImageAppApplication.database.imageDao().getImageModelbyId(id);

                imageModel.setViewedTime(System.currentTimeMillis());
                ImageAppApplication.database.imageDao().update(imageModel);
                return imageModel;
            }

            @Override
            protected void onPostExecute(ImageModel imageModel) {
                super.onPostExecute(imageModel);
                if(imageModel != null) {
                    mTitleTextView.setText(imageModel.getTitle());
                    mDescriptionTextView.setText(imageModel.getDescription());
                    if (imageModel.getType() == ImageModel.LOCAL_FILE) {
                        Picasso.get()
                                .load(new File(imageModel.getPath()))
                                .placeholder(R.drawable.image_not_available)
                                .fit()
                                .centerCrop()
                                .into(mImageView);
                    } else {
                        Picasso.get()
                                .load(imageModel.getPath())
                                .placeholder(R.drawable.image_not_available)
                                .fit()
                                .centerCrop()
                                .into(mImageView);
                    }
                }
            }
        }.execute();
    }
}
