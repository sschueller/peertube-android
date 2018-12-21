package net.schueller.peertube.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
//import com.google.android.gms.common.GooglePlayServicesRepairableException;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.security.ProviderInstaller;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

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

public class VideoListActivity extends AppCompatActivity {

    private String TAG = "VideoListActivity";

    public static final String EXTRA_VIDEOID = "VIDEOID ";

    private VideoAdapter videoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int currentStart = 0;
    private int count = 12;
    private String sort = "-createdAt";
    private String filter = "";
    private String searchQuery = "";

    private TextView emptyView;
    private RecyclerView recyclerView;

    private boolean isLoading = false;

    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                //Log.v(TAG, "navigation_home");

                if (!isLoading) {
                    sort = "-createdAt";
                    currentStart = 0;
                    loadVideos(currentStart, count, sort, filter);
                }

                return true;
            case R.id.navigation_trending:
                //Log.v(TAG, "navigation_trending");

                if (!isLoading) {
                    sort = "-trending";
                    currentStart = 0;
                    loadVideos(currentStart, count, sort, filter);
                }

                return true;
            case R.id.navigation_subscriptions:
                //Log.v(TAG, "navigation_subscriptions");
                Toast.makeText(VideoListActivity.this, "Subscriptions Not Implemented", Toast.LENGTH_SHORT).show();

                return false;

            case R.id.navigation_account:
                //Log.v(TAG, "navigation_account");
                Toast.makeText(VideoListActivity.this, "Account Not Implemented", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(this, LoginActivity.class);
//                this.startActivity(intent);

                return false;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Night Mode
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        AppCompatDelegate.setDefaultNightMode(sharedPref.getBoolean("pref_dark_mode", false) ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Set theme
        setTheme(getResources().getIdentifier(
                sharedPref.getString("pref_theme", "AppTheme.ORANGE"),
                "style",
                getPackageName())
        );

        setContentView(R.layout.activity_video_list);

        filter = "";

        // Init icons
        Iconify.with(new FontAwesomeModule());

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        // fix android trying to use SSLv3 for handshake
//        updateAndroidSecurityProvider(this);

        // Bottom Navigation
        BottomNavigationViewEx navigation = findViewById(R.id.navigation);

        navigation.enableAnimation(false);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED); // enableShiftingMode
        navigation.setItemHorizontalTranslationEnabled(false); // enableItemShiftingMode

        Menu navMenu = navigation.getMenu();
        navMenu.findItem(R.id.navigation_home).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_home)
                        .colorRes(R.color.primaryDarkColorDeepOrange));
        navMenu.findItem(R.id.navigation_trending).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_fire)
                        .colorRes(R.color.primaryDarkColorDeepOrange));
        navMenu.findItem(R.id.navigation_subscriptions).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_folder)
                        .colorRes(R.color.primaryDarkColorDeepOrange));
        navMenu.findItem(R.id.navigation_account).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_user_circle)
                        .colorRes(R.color.primaryDarkColorDeepOrange));

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // load Video List
        createList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Set an icon in the ActionBar
        menu.findItem(R.id.action_settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_cog)
                        .colorRes(R.color.cardview_light_background)
                        .actionBarSize());


        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_search)
                        .colorRes(R.color.cardview_light_background)
                        .actionBarSize());

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setQueryRefinementEnabled(true);

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchQuery = "";
                Log.d(TAG, "onMenuItemActionCollapse: ");
                loadVideos(0, count, sort, filter);
                return true;
            }
        });

        // TODO, this doesn't work
        searchManager.setOnDismissListener(() -> {
            searchQuery = "";
            Log.d(TAG, "onDismiss: ");
            loadVideos(0, count, sort, filter);
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_search:
                //Toast.makeText(this, "Search Selected", Toast.LENGTH_SHORT).show();

                return false;
            case R.id.action_settings:
//                Toast.makeText(this, "Login Selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SettingsActivity.class);
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

        emptyView = findViewById(R.id.empty_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoListActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        videoAdapter = new VideoAdapter(new ArrayList<>(), VideoListActivity.this);
        recyclerView.setAdapter(videoAdapter);

        loadVideos(currentStart, count, sort, filter);

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
                            loadVideos(currentStart, count, sort, filter);
                        }
                    }
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh items
            if (!isLoading) {
                currentStart = 0;
                loadVideos(currentStart, count, sort, filter);
            }
        });

    }

    private void loadVideos(int start, int count, String sort, String filter) {

        isLoading = true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String nsfw = sharedPref.getBoolean("pref_show_nsfw", false) ? "both" : "false";

        String apiBaseURL = APIUrlHelper.getUrlWithVersion(this);

        GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetVideoDataService.class);

        Call<VideoList> call;
        if (!searchQuery.equals("")) {
            call = service.searchVideosData(start, count, sort, nsfw, searchQuery);
        } else {
            call = service.getVideosData(start, count, sort, nsfw);
        }

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

                // no results show no results message
                if (currentStart == 0 && videoAdapter.getItemCount() == 0) {
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
     * <p>
     * //     * @param callingActivity Activity
     */
//    private void updateAndroidSecurityProvider(Activity callingActivity) {
//        try {
//            ProviderInstaller.installIfNeeded(this);
//        } catch (GooglePlayServicesRepairableException e) {
//            // Thrown when Google Play Services is not installed, up-to-date, or enabled
//            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
//            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
//        } catch (GooglePlayServicesNotAvailableException e) {
//            Log.e("SecurityException", "Google Play Services not available.");
//        }
//    }
    @Override
    protected void onResume() {
        super.onResume();
        // only check when we actually need the permission
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                sharedPref.getBoolean("pref_torrent_player", false)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionsProvider.AUTHORITY,
                    SearchSuggestionsProvider.MODE);

            // Save recent searches
            suggestions.saveRecentQuery(query, null);

            searchQuery = query;

            loadVideos(0, count, sort, filter);

        }
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        startSearch(null, false, appData, false);
        return true;
    }


}
