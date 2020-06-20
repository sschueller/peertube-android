package net.schueller.peertube.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import net.schueller.peertube.R;
import net.schueller.peertube.activity.LoginActivity;
import net.schueller.peertube.activity.MeActivity;
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

                            } else {
                                Log.wtf(TAG, response2.toString());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Token> call2, @NonNull Throwable t2) {
                            Log.wtf("err", t2.fillInStackTrace());
                        }
                    });

                } else {
                    Log.wtf(TAG, response.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<OauthClient> call, @NonNull Throwable t) {

                Log.d("err", call.toString());

                Log.wtf("err", t.fillInStackTrace());
            }
        });
    }
}
