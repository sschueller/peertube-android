package net.schueller.peertube.network;

import net.schueller.peertube.model.Config;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GetConfigDataService {

    @GET("config")
    Call<Config> getConfigData();

}