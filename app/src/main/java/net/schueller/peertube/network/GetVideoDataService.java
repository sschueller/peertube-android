package net.schueller.peertube.network;

import net.schueller.peertube.model.VideoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetVideoDataService {
    @GET("videos/")
    Call<VideoList> getVideoData(
            @Query("start") int start,
            @Query("count") int count,
            @Query("sort") String sort
    );
}