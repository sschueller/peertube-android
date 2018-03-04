package net.schueller.peertube.network;

import net.schueller.peertube.model.Video;
import net.schueller.peertube.model.VideoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetVideoDataService {
    @GET("videos/")
    Call<VideoList> getVideosData(
            @Query("start") int start,
            @Query("count") int count,
            @Query("sort") String sort
    );

    @GET("videos/{id}")
    Call<Video> getVideoData(
            @Path(value = "id", encoded = true) String id
    );
}