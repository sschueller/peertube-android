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
