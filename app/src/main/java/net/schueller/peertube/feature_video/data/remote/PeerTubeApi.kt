package net.schueller.peertube.feature_video.data.remote

import net.schueller.peertube.common.Resource
import net.schueller.peertube.feature_video.data.remote.dto.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*



interface PeerTubeApi {
    @GET("videos/")
    suspend fun getVideos(
        @Query("start") start: Int,
        @Query("count") count: Int,
        @Query("sort") sort: String? = null,
        @Query("nsfw") nsfw: String? = null,
        @Query("filter") filter: String? = null,
        @Query("languageOneOf") languages: Set<String?>?
    ): VideoListDto

    @GET("videos/{uuid}")
    suspend fun getVideo(
        @Path(value = "uuid", encoded = true) id: String
    ): VideoDto

    @GET("search/videos/")
    suspend fun searchVideos(
        @Query("start") start: Int,
        @Query("count") count: Int,
        @Query("sort") sort: String?,
        @Query("nsfw") nsfw: String?,
        @Query("search") search: String?,
        @Query("filter") filter: String?,
        @Query("languageOneOf") languages: Set<String?>?
    ): VideoListDto

    @GET("users/me/videos/{id}/rating")
    suspend fun getVideoRating(
        @Path(value = "id") id: Int?
    ): RatingDto

    @GET("videos/{uuid}/description")
    suspend fun getVideoFullDescription(
        @Path(value = "uuid", encoded = true) id: String?
    ): DescriptionDto

    // Rate video
    @PUT("videos/{id}/rate")
    suspend fun rateVideo(
        @Path(value = "id", encoded = true) id: Int?,
        @Body params: RequestBody?
    ): Response<Unit> // https://github.com/square/retrofit/issues/3075

    @GET("accounts/{displayNameAndHost}/videos")
    suspend fun getAccountVideos(
        @Path(value = "displayNameAndHost", encoded = true) displayNameAndHost: String?,
        @Query("start") start: Int,
        @Query("count") count: Int,
        @Query("sort") sort: String?
    ): VideoListDto

    @GET("accounts/{accountName}/video-playlists")
    suspend fun getAccountVideoPlaylists(
        @Path(value = "accountName", encoded = true) accountName: String?,
        @Query("start") start: Int,
        @Query("count") count: Int,
        @Query("sort") sort: String? = null
    ): VideoListDto

    @GET("overviews/videos")
    suspend fun getOverviewVideos(
        @Query("page") page: Int
    ): OverviewDto

    // https://troll.tv/api/v1/videos/{id}/comment-threads?start=0&count=10&sort=-createdAt
    @GET("videos/{id}/comment-threads")
    suspend fun getCommentThreads(
        @Path(value = "id", encoded = true) id: Int?,
        @Query("start") start: Int,
        @Query("count") count: Int,
        @Query("sort") sort: String?
    ): CommentThreadDto


    // Auth
    @GET("oauth-clients/local")
    suspend fun getOauthClientLocal(): OauthClientDto

    @FormUrlEncoded
    @POST("users/token")
    suspend fun getAuthenticationToken(
        @Field("client_id") clientId: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("response_type") responseType: String?,
        @Field("grant_type") grantType: String?,
        @Field("scope") scope: String?,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Response<TokenDto>

    @POST("users/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("client_id") clientId: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("grant_type") grantType: String?,
        @Field("response_type") responseType: String?,
        @Field("username") username: String?,
        @Field("refresh_token") refreshToken: String?
    ): Response<TokenDto>

    @GET("users/me")
    suspend fun getMe(): MeDto

}


