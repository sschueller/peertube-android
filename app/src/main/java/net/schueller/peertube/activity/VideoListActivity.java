package net.schueller.peertube.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import net.schueller.peertube.R;
import net.schueller.peertube.adapter.VideoAdapter;
import net.schueller.peertube.model.VideoList;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoListActivity extends AppCompatActivity {

    private String TAG = "VideoListActivity";

    public static final String EXTRA_VIDEOID = "VIDEOID ";

    private VideoAdapter videoAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;

    private int currentStart = 0;
    private int count = 12;
    private String sort = "-createdAt";

    private boolean isLoading = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        //Log.v(TAG, "navigation_home");

                        if (!isLoading) {
                            sort = "-createdAt";
                            currentStart = 0;
                            loadVideos(currentStart, count, sort);
                        }

                        return true;
                    case R.id.navigation_trending:
                        //Log.v(TAG, "navigation_trending");

                        if (!isLoading) {
                            sort = "-views";
                            currentStart = 0;
                            loadVideos(currentStart, count, sort);
                        }

                        return true;
                    case R.id.navigation_subscriptions:
                        Log.v(TAG, "navigation_subscriptions");
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        // Init icons
        Iconify.with(new FontAwesomeModule());

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        // fix android trying to use SSLv3 for handshake
        updateAndroidSecurityProvider(this);

        // Bottom Navigation
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Menu navMenu = navigation.getMenu();
        navMenu.findItem(R.id.navigation_home).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_home));
        navMenu.findItem(R.id.navigation_trending).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_fire));
        navMenu.findItem(R.id.navigation_subscriptions).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_folder));
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        createList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Set an icon in the ActionBar
        menu.findItem(R.id.action_user).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_user_o)
                        .colorRes(R.color.cardview_light_background)
                        .actionBarSize());

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_user:
//                Toast.makeText(this, "Login Selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);

                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createList() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoListActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        videoAdapter = new VideoAdapter(new ArrayList<>(), VideoListActivity.this);
        recyclerView.setAdapter(videoAdapter);

        loadVideos(currentStart, count, sort);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    // is at end of list?
                    if(!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)){
                        if (!isLoading) {
                            currentStart = currentStart + count;
                            loadVideos(currentStart, count, sort);
                        }
                    }
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh items
            if (!isLoading) {
                currentStart = 0;
                loadVideos(currentStart, count, sort);
            }
        });

    }

    private void loadVideos(int start, int count, String sort) {

        isLoading = true;

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String defaultApiURL = getResources().getString(R.string.api_base_url);
        String apiBaseURL = sharedPref.getString(getString(R.string.api_url_key_key), defaultApiURL);

        GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL + "/api/v1/").create(GetVideoDataService.class);

        Call<VideoList> call = service.getVideosData(start, count, sort);

        /*Log the URL called*/
        Log.d("URL Called", call.request().url() + "");
        Toast.makeText(VideoListActivity.this, "URL Called: " + call.request().url(), Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<VideoList>() {
            @Override
            public void onResponse(@NonNull Call<VideoList> call, @NonNull Response<VideoList> response) {

                if (currentStart == 0) {
                    videoAdapter.clearData();
                }

                videoAdapter.setData(response.body().getVideoArrayList());
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<VideoList> call, @NonNull Throwable t) {
                Log.wtf("err", t.fillInStackTrace());
                Toast.makeText(VideoListActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Force android to not use SSLv3
     *
     * @param callingActivity Activity
     */
    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }
}
