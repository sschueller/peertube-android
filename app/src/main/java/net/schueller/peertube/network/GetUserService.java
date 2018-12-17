package net.schueller.peertube.network;

import net.schueller.peertube.model.Me;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GetUserService {

    @GET("users/me")
    Call<Me> getMe(@Header("Authorization") String authorization);
}