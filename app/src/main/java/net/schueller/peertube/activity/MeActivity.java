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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import net.schueller.peertube.R;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.Me;
import net.schueller.peertube.network.GetUserService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.network.Session;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeActivity extends CommonActivity {


    private static final String TAG = "MeActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_top_account, menu);

        // Set an icon in the ActionBar
        menu.findItem(R.id.action_logout).setIcon(
                new IconicsDrawable(this, FontAwesome.Icon.faw_sign_out_alt).actionBar());

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected

            case R.id.action_logout:
                Session.getInstance().invalidate();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(intent);
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(
                new IconicsDrawable(this, FontAwesome.Icon.faw_chevron_left).actionBar()
        );


        init();
    }

    private void init() {
        // try to get user data
        getUserData();
    }

    private boolean getUserData() {

        // TODO


        String apiBaseURL = APIUrlHelper.getUrlWithVersion(this);

        GetUserService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetUserService.class);

        Call<Me> call = service.getMe();

        call.enqueue(new Callback<Me>() {
            @Override
            public void onResponse(@NonNull Call<Me> call, @NonNull Response<Me> response) {

                if (response.isSuccessful()) {

                    Me me = response.body();

                    TextView username = findViewById(R.id.account_username);
                    TextView email = findViewById(R.id.account_email);

                    username.setText(me.getUsername());
                    email.setText(me.getEmail());

                    Log.v(TAG, me.getEmail());

                }


            }

            @Override
            public void onFailure(Call<Me> call, Throwable t) {

            }
        });

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();

    }
}
