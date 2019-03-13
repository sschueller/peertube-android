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

package net.schueller.peertube.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import net.schueller.peertube.R;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.OauthClient;
import net.schueller.peertube.model.Token;
import net.schueller.peertube.network.AuthenticationService;
import net.schueller.peertube.network.RetrofitInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends CommonActivity {

    private String TAG = "LoginActivity";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // bind button click
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);


        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.tool_bar_login);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(
                new IconicsDrawable(this, FontAwesome.Icon.faw_chevron_left).actionBar()
        );

    }

    @Override
    public void onResume() {

        EditText mServerName = findViewById(R.id.login_current_server);
        mServerName.setText(APIUrlHelper.getUrl(this));
        mServerName.setInputType(InputType.TYPE_NULL);

        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up

        return false;
    }

    private void attemptLogin() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Context context = this;

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        //check values
        if (email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email/Username is empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_LONG).show();
            return;
        }
        // make http call to login and save access tokens

        String apiBaseURL = APIUrlHelper.getUrlWithVersion(this);

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
                            email,
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

                                editor.putString(getString(R.string.pref_token_access), token.getAccessToken());
                                editor.putString(getString(R.string.pref_token_refresh), token.getExpiresIn());
                                editor.putString(getString(R.string.pref_token_type), token.getTokenType());
                                editor.commit();

                                Log.wtf(TAG, "Logged in");

                                Intent intent = new Intent(context, MeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);

                                finish(); // close this activity

                            } else {
                                Log.wtf(TAG, response2.toString());

                                Toast.makeText(LoginActivity.this, "Login Error!", Toast.LENGTH_LONG).show();

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
                Log.wtf("err", t.fillInStackTrace());
            }
        });
    }


}

