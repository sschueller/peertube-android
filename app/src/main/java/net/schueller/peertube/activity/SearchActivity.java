package net.schueller.peertube.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;


import net.schueller.peertube.R;
import net.schueller.peertube.adapter.VideoAdapter;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.VideoList;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.provider.SearchSuggestionsProvider;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: cleanup, this code partially is duplicated from VideoList Activity and should be seperated out

public class SearchActivity extends AppCompatActivity {

    private VideoAdapter videoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int currentStart = 0;
    private int count = 12;
    private String sort = "-match";
    private String filter = "";

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent  = getIntent();

        // do search
        handleIntent(intent);

        // handle search suggestions
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionsProvider.AUTHORITY,
                    SearchSuggestionsProvider.MODE);
            // Save recent searches
            suggestions.saveRecentQuery(query, null);
        }

    }

    private void createList(String query) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        videoAdapter = new VideoAdapter(new ArrayList<>(), SearchActivity.this);
        recyclerView.setAdapter(videoAdapter);

        loadVideos(currentStart, count, sort, query, filter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    // is at end of list?
                    if(!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)){
                        if (!isLoading) {
                            currentStart = currentStart + count;
                            loadVideos(currentStart, count, sort, query, filter);
                        }
                    }
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh items
            if (!isLoading) {
                currentStart = 0;
                loadVideos(currentStart, count, sort, query, filter);
            }
        });

    }


    private void loadVideos(int start, int count, String sort, String search, String filter) {

        isLoading = true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String nsfw = sharedPref.getBoolean("pref_show_nsfw", false) ? "both" : "false";

        String apiBaseURL = APIUrlHelper.getUrlWithVersion(this);

        GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetVideoDataService.class);

        Call<VideoList> call = service.searchVideosData(start, count, sort, nsfw, search);

        /*Log the URL called*/
        Log.d("URL Called", call.request().url() + "");
//        Toast.makeText(VideoListActivity.this, "URL Called: " + call.request().url(), Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<VideoList>() {
            @Override
            public void onResponse(@NonNull Call<VideoList> call, @NonNull Response<VideoList> response) {

                if (currentStart == 0) {
                    videoAdapter.clearData();
                }

                if (response.body() != null) {
                    videoAdapter.setData(response.body().getVideoArrayList());
                }
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<VideoList> call, @NonNull Throwable t) {
                Log.wtf("err", t.fillInStackTrace());
                Toast.makeText(SearchActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }



    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v("Search Activity", query);
            createList(query);
        }
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        startSearch(null, false, appData, false);
        return true;
    }
}
