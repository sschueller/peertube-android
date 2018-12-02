package net.schueller.peertube.activity;

import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.schueller.peertube.R;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.OauthClient;
import net.schueller.peertube.model.Token;
import net.schueller.peertube.model.VideoList;
import net.schueller.peertube.network.AuthenticationService;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String TAG = "LoginActivity";


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // bind button click
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }

    }



    private void attemptLogin() {


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
            public void onResponse(@NonNull Call<OauthClient> call, @NonNull retrofit2.Response<OauthClient> response) {

                if (response.body() != null) {

                    Call<Token> call2 = service.getAuthenticationToken(
                        response.body().getClientId(),
                        response.body().getClientSecret(),
                            "code",
                            "password",
                            "upload",
                            email,
                            password
                    );
                    call2.enqueue(new Callback<Token>() {
                        @Override
                        public void onResponse(@NonNull Call<Token> call2, @NonNull retrofit2.Response<Token> response2) {

                            if (response2.body() != null) {
                                Log.wtf(TAG, response2.body().getAccessToken());
                                Log.wtf(TAG, response2.body().getExpiresIn());
                                Log.wtf(TAG, response2.body().getRefreshToken());
                                Log.wtf(TAG, response2.body().getTokenType());
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

