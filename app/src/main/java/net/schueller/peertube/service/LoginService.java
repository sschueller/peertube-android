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
package net.schueller.peertube.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import net.schueller.peertube.R;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.OauthClient;
import net.schueller.peertube.model.Token;
import net.schueller.peertube.network.AuthenticationService;
import net.schueller.peertube.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.schueller.peertube.application.AppApplication.getContext;

public class LoginService {

    private static final String TAG = "LoginService";

    public static void Authenticate(String username, String password)
    {
        Context context = getContext();

        String apiBaseURL = APIUrlHelper.getUrlWithVersion(context);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        AuthenticationService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(AuthenticationService.class);

        Call<OauthClient> call = service.getOauthClientLocal();

        call.enqueue(new Callback<OauthClient>() {
            @Override
            public void onResponse(@NonNull Call<OauthClient> call, @NonNull Response<OauthClient> response) {

                if (response.isSuccessful()) {

                    OauthClient oauthClient = response.body();

                    Call<Token> call2 = service.getAuthenticationToken(
                            oauthClient.getClientId(),
                            oauthClient.getClientSecret(),
                            "code",
                            "password",
                            "upload",
                            username,
                            password
                    );
                    call2.enqueue(new Callback<Token>() {
                        @Override
                        public void onResponse(@NonNull Call<Token> call2, @NonNull retrofit2.Response<Token> response2) {

                            if (response2.isSuccessful()) {

                                Token token = response2.body();

                                SharedPreferences.Editor editor = sharedPref.edit();

                                // TODO: calc expiration
                                //editor.putInt(getString(R.string.pref_token_expiration), token.getRefreshToken());

                                editor.putString(context.getString(R.string.pref_token_access), token.getAccessToken());
                                editor.putString(context.getString(R.string.pref_token_refresh), token.getExpiresIn());
                                editor.putString(context.getString(R.string.pref_token_type), token.getTokenType());
                                editor.apply();

                                Log.wtf(TAG, "Logged in");

                                Toast.makeText(context, context.getString(R.string.authentication_login_success), Toast.LENGTH_LONG).show();


                            } else {
                                Log.wtf(TAG, response2.toString());
                                Toast.makeText(context, context.getString(R.string.authentication_login_failed), Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Token> call2, @NonNull Throwable t2) {
                            Log.wtf("err", t2.fillInStackTrace());
                            Toast.makeText(context, context.getString(R.string.authentication_login_failed), Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    Log.wtf(TAG, response.toString());
                    Toast.makeText(context, context.getString(R.string.authentication_login_failed), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<OauthClient> call, @NonNull Throwable t) {
                Log.wtf("err", t.fillInStackTrace());
                Toast.makeText(context, context.getString(R.string.authentication_login_failed), Toast.LENGTH_LONG).show();

            }
        });
    }
}
