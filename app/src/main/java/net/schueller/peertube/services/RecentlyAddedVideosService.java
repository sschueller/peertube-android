package net.schueller.peertube.services;


import com.google.gson.annotations.SerializedName;

import net.schueller.peertube.model.Video;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public final class RecentlyAddedVideosService {
    public static final String API_URL = "https://troll.tv/api/v1";

    public interface RecentlyAddedVideos {
        @GET("/videos/?start=0&count=12&sort=-createdAt")
        @SerializedName("data")
        Call<List<Video>> videos();
    }

    public static void main(String... args) throws IOException {
        // Create a very simple REST adapter which points the PeerTube API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of our GitHub API interface.
        RecentlyAddedVideos recentlyAddedVideos = retrofit.create(RecentlyAddedVideos.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<List<Video>> call = recentlyAddedVideos.videos();

        // Fetch and print a list of the contributors to the library.
        List<Video> videos = call.execute().body();
        for (Video video : videos) {
            System.out.println(video.getName());
        }
    }
}