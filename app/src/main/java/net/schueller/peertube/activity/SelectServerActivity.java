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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import net.schueller.peertube.R;
import net.schueller.peertube.model.ServerList;
import net.schueller.peertube.network.GetServerListDataService;
import net.schueller.peertube.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectServerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_server);

        // get list of peertube servers

        // TODO: Get here via settings, get data from API, add to adapter and show in recycle view, upon selection fill settings field

        GetServerListDataService service = RetrofitInstance.getRetrofitInstance("https://instances.joinpeertube.org/api/v1/").create(GetServerListDataService.class);
        Call<ServerList> call = service.getInstancesData(0, 500);
        call.enqueue(new Callback<ServerList>() {
            @Override
            public void onResponse(@NonNull Call<ServerList> call, @NonNull Response<ServerList> response) {
                // response.body().getVideoArrayList();
            }

            @Override
            public void onFailure(@NonNull Call<ServerList> call, @NonNull Throwable t) {

            }
        });


    }
}
