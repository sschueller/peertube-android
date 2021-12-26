/*
 * Copyright (C) 2020 Stefan Schüller <sschueller@techdroid.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.network;

import net.schueller.peertube.model.Description;
import net.schueller.peertube.model.Overview;
import net.schueller.peertube.model.Rating;
import net.schueller.peertube.model.Video;
import net.schueller.peertube.model.VideoList;

import java.util.Set;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetVideoDataService {
    @GET("videos/")
    Call<VideoList> getVideosData(
            @Query("start") int start,
            @Query("count") int count,
            @Query("sort") String sort,
            @Query("nsfw") String nsfw,
            @Query("filter") String filter,
            @Query("languageOneOf") Set<String> languages
    );

    @GET("videos/{uuid}")
    Call<Video> getVideoData(
            @Path(value = "uuid", encoded = true) String id
    );

    @GET("search/videos/")
    Call<VideoList> searchVideosData(
            @Query("start") int start,
            @Query("count") int count,
            @Query("sort") String sort,
            @Query("nsfw") String nsfw,
            @Query("search") String search,
            @Query("filter") String filter,
            @Query("languageOneOf") Set<String> languages
    );

    @GET("users/me/videos/{id}/rating")
    Call<Rating> getVideoRating(
            @Path(value = "id", encoded = true) Integer id
    );

    @GET("videos/{uuid}/description")
    Call<Description> getVideoFullDescription(
            @Path(value = "uuid", encoded = true) String id
    );

    @PUT("videos/{id}/rate")
    Call<ResponseBody> rateVideo(
            @Path(value = "id", encoded = true) Integer id,
            @Body RequestBody params
    );

    // https://troll.tv/api/v1/accounts/theouterlinux@peertube.mastodon.host/videos?start=0&count=8&sort=-publishedAt

    @GET("accounts/{displayNameAndHost}/videos")
    Call<VideoList> getAccountVideosData(
            @Path(value = "displayNameAndHost", encoded = true) String displayNameAndHost,
            @Query("start") int start,
            @Query("count") int count,
            @Query("sort") String sort
    );

    @GET("overviews/videos")
    Call<Overview> getOverviewVideosData(
            @Query("page") int page
    );

}