package net.schueller.peertube.model;

import com.google.gson.annotations.SerializedName;

public class Rating {

    @SerializedName("videoId")
    private Integer videoId;

    @SerializedName("rating")
    private String rating;

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
