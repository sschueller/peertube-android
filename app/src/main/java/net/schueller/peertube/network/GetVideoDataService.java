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
            @Query("sort") String sort,
            @Query("nsfw") String nsfw
            //@Query("filter") String filter
    );

    @GET("videos/{id}")
    Call<Video> getVideoData(
            @Path(value = "id", encoded = true) String id
    );

    @GET("videos/search/")
    Call<VideoList> searchVideosData(
            @Query("start") int start,
            @Query("count") int count,
            @Query("sort") String sort,
            @Query("filter") String filter,
            @Query("search") String search
    );
}