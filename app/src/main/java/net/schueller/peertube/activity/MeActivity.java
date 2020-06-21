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

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.schueller.peertube.R;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.Avatar;
import net.schueller.peertube.model.Me;
import net.schueller.peertube.network.GetUserService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.network.Session;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.schueller.peertube.application.AppApplication.getContext;

public class MeActivity extends CommonActivity {


    private static final String TAG = "MeActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_top_account, menu);

        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_me);

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.tool_bar_me);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        LinearLayout account = findViewById(R.id.a_me_account_line);
        LinearLayout settings = findViewById(R.id.a_me_settings);
        LinearLayout help = findViewById(R.id.a_me_helpnfeedback);

        TextView logout = findViewById(R.id.a_me_logout);


        settings.setOnClickListener(view -> {
            Intent settingsActivity = new Intent(getContext(), SettingsActivity.class);
            //overridePendingTransition(R.anim.slide_in_bottom, 0);
            startActivity(settingsActivity);
        });

        help.setOnClickListener(view -> {
            String url = "https://github.com/sschueller/peertube-android/issues";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        logout.setOnClickListener(view -> {
            Session.getInstance().invalidate();
            account.setVisibility(View.GONE);

        });

        getUserData();
    }

    private void getUserData() {


        String apiBaseURL = APIUrlHelper.getUrlWithVersion(this);
        String baseURL = APIUrlHelper.getUrl(this);

        GetUserService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetUserService.class);

        Call<Me> call = service.getMe();

        call.enqueue(new Callback<Me>() {

            LinearLayout account = findViewById(R.id.a_me_account_line);

            @Override
            public void onResponse(@NonNull Call<Me> call, @NonNull Response<Me> response) {


                if (response.isSuccessful()) {

                    Me me = response.body();

                    Log.d(TAG, response.body().toString());

                    TextView username = findViewById(R.id.a_me_username);
                    TextView email = findViewById(R.id.a_me_email);
                    ImageView avatarView = findViewById(R.id.a_me_avatar);


                    username.setText(me.getUsername());
                    email.setText(me.getEmail());

                    Avatar avatar = me.getAccount().getAvatar();
                    if (avatar != null) {
                        String avatarPath = avatar.getPath();
                        Picasso.get()
                                .load(baseURL + avatarPath)
                                .into(avatarView);
                    }

                    account.setVisibility(View.VISIBLE);

                } else {
                    account.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<Me> call, @NonNull Throwable t) {
                account.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserData();

    }
}
