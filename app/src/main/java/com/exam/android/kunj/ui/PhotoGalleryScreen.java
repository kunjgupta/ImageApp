package com.exam.android.kunj.ui;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.exam.android.kunj.ImageAppApplication;
import com.exam.android.kunj.adapters.ImageAdapter;
import com.exam.android.kunj.adapters.ListAdapter;
import com.exam.android.kunj.db.models.ImageModel;
import com.exam.android.kunj.ui.supportView.CustomGridView;
import com.exam.android.kunj.utils.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.exam.android.kunj.R;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */

public class PhotoGalleryScreen extends AppCompatActivity {
    @BindView(R.id.gridview) CustomGridView mGridView;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_gallery_screen);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
            actionBar.setTitle(R.string.photo_gallery_screen_action_bar_title);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // specify an adapter (see also next example)
        mAdapter = new ListAdapter(new OnItemClickListener() {
            @Override
            public void onItemClick(int imageModelId) {
                Intent intent = new Intent(PhotoGalleryScreen.this, ItemDetailScreen.class);
                intent.putExtra(Constant.IMAGE_MODEL_ID, imageModelId);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @OnClick(R.id.fab)
    void clickOnFab() {
        Intent intent = new Intent(this, AddItemScreen.class);
        startActivity(intent);
    }

    public interface OnItemClickListener {
        void onItemClick(int imageModelId);
    }

    private void getData() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ImageAppApplication.database.imageDao().getRecentViewedItems().observe(PhotoGalleryScreen.this,
                        new Observer<List<ImageModel>>() {
                            @Override
                            public void onChanged(@Nullable List<ImageModel> imageModelList) {
                                ImageAdapter imageAdapter = new ImageAdapter(PhotoGalleryScreen.this, imageModelList);
                                mGridView.setAdapter(imageAdapter);
                            }
                        });

                ImageAppApplication.database.imageDao().getAll().observe(PhotoGalleryScreen.this,
                        new Observer<List<ImageModel>>() {
                            @Override
                            public void onChanged(@Nullable List<ImageModel> imageModelList) {
                                mAdapter.setListItem(imageModelList);
                            }
                        });
                return null;
            }
        }.execute();
    }
}