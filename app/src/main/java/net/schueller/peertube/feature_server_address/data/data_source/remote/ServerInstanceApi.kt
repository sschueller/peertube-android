package net.schueller.peertube.feature_server_address.data.data_source.remote

import net.schueller.peertube.feature_server_address.data.data_source.remote.dto.ServerListDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerInstanceApi {
    @GET("instances/")
    suspend fun getServers(
        @Query("start") start: Int,
        @Query("count") count: Int,
        @Query("search") text: String?
    ): ServerListDto
}