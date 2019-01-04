package com.exam.android.kunj.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.exam.android.kunj.db.Dao.ImageDao;
import com.exam.android.kunj.db.models.ImageModel;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
@Database(entities = {ImageModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "image_app-database.db";

    private static AppDatabase INSTANCE;

    public abstract ImageDao imageDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            Log.e("AppDatabase", "getAppDatabase: INSTANCE is created");
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, DATABASE_NAME).build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
