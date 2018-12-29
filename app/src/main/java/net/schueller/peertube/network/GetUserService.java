package net.schueller.peertube.network;

import net.schueller.peertube.model.Me;
import net.schueller.peertube.model.VideoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GetUserService {

    @GET("users/me")
    Call<Me> getMe(@Header("Authorization") String authorization);

    @GET("users/me/subscriptions/videos")
    Call<VideoList> getVideosSubscripions(
            @Query("start") int start,
            @Query("count") int count,
            @Query("sort") String sort
    );

}