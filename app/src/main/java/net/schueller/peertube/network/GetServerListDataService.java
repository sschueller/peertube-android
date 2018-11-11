package net.schueller.peertube.network;

import net.schueller.peertube.model.ServerList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetServerListDataService {
    @GET("instances/")
    Call<ServerList> getInstancesData(
            @Query("start") int start,
            @Query("count") int count
    );

}