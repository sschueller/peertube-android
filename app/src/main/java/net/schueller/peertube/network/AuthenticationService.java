/*
 * Copyright 2018 Stefan Schüller <sschueller@techdroid.com>
 *
 * License: GPL-3.0+
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.network;

import net.schueller.peertube.model.OauthClient;
import net.schueller.peertube.model.Token;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthenticationService {
    @GET("oauth-clients/local")
    Call<OauthClient> getOauthClientLocal();

    @FormUrlEncoded
    @POST("users/token")
    Call<Token> getAuthenticationToken(
        @Field("client_id") String clientId,
        @Field("client_secret") String clientSecret,
        @Field("response_type") String responseType,
        @Field("grant_type") String grantType,
        @Field("scope") String scope,
        @Field("username") String username,
        @Field("password") String password
    );

}