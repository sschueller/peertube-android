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