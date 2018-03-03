package net.schueller.peertube.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class VideoList {

    @SerializedName("data")
    private ArrayList<Video> videoList;

    public ArrayList<Video> getVideoArrayList() {
        return videoList;
    }

    public void setEmployeeArrayList(ArrayList<Video> videoArrayList) {
        this.videoList = videoArrayList;
    }
}