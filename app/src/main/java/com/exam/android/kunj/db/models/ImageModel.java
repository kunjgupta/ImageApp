package com.exam.android.kunj.db.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
@Entity(tableName = "image_model")
public class ImageModel {
    public static final int LOCAL_FILE = 1;
    public static final int SERVER_FILE = 2;
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("path")
    @Expose
    private String path;

    @ColumnInfo(name = "viewed_time")
    private long viewedTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getViewedTime() {
        return viewedTime;
    }

    public void setViewedTime(long viewedTime) {
        this.viewedTime = viewedTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", path='" + path + '\'' +
                ", viewedTime=" + viewedTime +
                '}';
    }
}
