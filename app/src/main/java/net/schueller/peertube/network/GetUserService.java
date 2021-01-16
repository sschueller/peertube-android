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

import net.schueller.peertube.model.Account;
import net.schueller.peertube.model.Channel;
import net.schueller.peertube.model.ChannelList;
import net.schueller.peertube.model.Me;
import net.schueller.peertube.model.VideoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetUserService {

    @GET("users/me")
    Call<Me> getMe();

    @GET("users/me/subscriptions/videos")
    Call<VideoList> getVideosSubscripions(
            @Query("start") int start,
            @Query("count") int count,
            @Query("sort") String sort
    );


    @GET("accounts/{displayName}")
    Call<Account> getAccount(
            @Path(value = "displayName", encoded = true) String displayName
    );


    @GET("accounts/{displayName}/video-channels")
    Call<ChannelList> getAccountChannels(
            @Path(value = "displayName", encoded = true) String displayName
    );


}