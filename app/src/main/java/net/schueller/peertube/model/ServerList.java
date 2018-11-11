package net.schueller.peertube.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServerList {

    @SerializedName("data")
    private ArrayList<Server> serverList;

    public ArrayList<Server> getServerArrayList() {
        return serverList;
    }

}