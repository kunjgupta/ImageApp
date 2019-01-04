package com.exam.android.kunj.db.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.exam.android.kunj.db.models.ImageModel;

import java.util.List;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
@Dao
public interface ImageDao {

    @Query("SELECT * FROM image_model")
    LiveData<List<ImageModel>> getAll();

    @Query("SELECT * FROM image_model order by viewed_time DESC limit 4")
    LiveData<List<ImageModel>> getRecentViewedItems();

    @Query("SELECT * FROM image_model where id =:imageModelId")
    ImageModel getImageModelbyId(int imageModelId);

    @Query("SELECT COUNT(*) from image_model")
    int getItemCount();

    @Insert
    void insertAll(List<ImageModel> imageModels);

    @Insert
    void insert(ImageModel imageModel);

    @Update
    void update(ImageModel imageModel);

}
