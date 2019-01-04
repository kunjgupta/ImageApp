package com.exam.android.kunj.db.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
public class ResultModel {
    @SerializedName("results")
    @Expose
    private List<ImageModel> results = null;

    public List<ImageModel> getResults() {
        return results;
    }

    public void setResults(List<ImageModel> results) {
        this.results = results;
    }
}
