package net.schueller.peertube.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.schueller.peertube.R;
import net.schueller.peertube.adapter.ServerAdapter;
import net.schueller.peertube.adapter.VideoAdapter;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.ServerList;
import net.schueller.peertube.model.VideoList;
import net.schueller.peertube.network.GetServerListDataService;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;

import java.util.ArrayList;

public class SelectServerActivity extends AppCompatActivity {

    private ServerAdapter serverAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int currentStart = 0;
    private int count = 12;

    private TextView emptyView;
    private RecyclerView recyclerView;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_selection);

        loadList();
    }


    private void loadList() {

        recyclerView = findViewById(R.id.serverRecyclerView);
        swipeRefreshLayout = findViewById(R.id.serversSwipeRefreshLayout);

        emptyView = findViewById(R.id.empty_server_selection_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SelectServerActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        serverAdapter = new ServerAdapter(new ArrayList<>(), SelectServerActivity.this);
        recyclerView.setAdapter(serverAdapter);

        loadServers(currentStart, count);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    // is at end of list?
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (!isLoading) {
                            currentStart = currentStart + count;
                            loadServers(currentStart, count);
                        }
                    }
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh items
            if (!isLoading) {
                currentStart = 0;
                loadServers(currentStart, count);
            }
        });


    }



    private void loadServers(int start, int count) {
        isLoading = true;

        GetServerListDataService service = RetrofitInstance.getRetrofitInstance(
                APIUrlHelper.getServerIndexUrl(SelectServerActivity.this)
        ).create(GetServerListDataService.class);


        Call<ServerList> call;

        call = service.getInstancesData(start, count);

        Log.d("URL Called", call.request().url() + "");

        call.enqueue(new Callback<ServerList>() {
            @Override
            public void onResponse(@NonNull Call<ServerList> call, @NonNull Response<ServerList> response) {

                if (currentStart == 0) {
                    serverAdapter.clearData();
                }

                if (response.body() != null) {
                    serverAdapter.setData(response.body().getServerArrayList());
                }

                // no results show no results message
                if (currentStart == 0 && serverAdapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                } else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ServerList> call, @NonNull Throwable t) {
                Log.wtf("err", t.fillInStackTrace());
                Toast.makeText(SelectServerActivity.this, getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
