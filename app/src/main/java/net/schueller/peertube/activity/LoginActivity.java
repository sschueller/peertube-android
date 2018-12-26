package net.schueller.peertube.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import net.schueller.peertube.R;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.OauthClient;
import net.schueller.peertube.model.Token;
import net.schueller.peertube.network.AuthenticationService;
import net.schueller.peertube.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.schueller.peertube.helper.Constants.DEFAULT_THEME;
import static net.schueller.peertube.helper.Constants.THEME_PREF_KEY;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "LoginActivity";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set theme
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(getResources().getIdentifier(
                sharedPref.getString(THEME_PREF_KEY, DEFAULT_THEME),
                "style",
                getPackageName())
        );

        setContentView(R.layout.activity_login);

        // bind button click
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

    }


    private void attemptLogin() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

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
                Log.wtf("err", t.fillInStackTrace());
            }
        });
    }


}

