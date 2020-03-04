package com.capstone.Retrofit_Services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @SerializedName("_id")
    @Expose
    private String _id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("user")
    @Expose
    private String user;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    @SerializedName("classification")
    @Expose
    private List<Classification> classification;

    public Result(String _id, String title, String user, String imageUrl, List<Classification> classification) {
        this._id = _id;
        this.title = title;
        this.user = user;
        this.imageUrl = imageUrl;
        this.classification = classification;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Classification> getClassification() {
        return classification;
    }

    public void setClassification(List<Classification> classification) {
        this.classification = classification;
    }
}
