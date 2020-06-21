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

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import net.schueller.peertube.R;
import net.schueller.peertube.adapter.VideoAdapter;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.VideoList;
import net.schueller.peertube.network.GetUserService;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.network.Session;
import net.schueller.peertube.provider.SearchSuggestionsProvider;
import net.schueller.peertube.service.VideoPlayerService;


import java.util.ArrayList;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoListActivity extends CommonActivity {

    private String TAG = "VideoListActivity";

    public static final String EXTRA_VIDEOID = "VIDEOID";
    public static final String EXTRA_ACCOUNTDISPLAYNAME = "ACCOUNTDISPLAYNAMEANDHOST";
    public static final Integer SWITCH_INSTANCE = 2;

    private VideoAdapter videoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int currentStart = 0;
    private int count = 12;
    private String sort = "-createdAt";
    private String filter = null;
    private String searchQuery = "";
    private Boolean subscriptions = false;

    private TextView emptyView;
    private RecyclerView recyclerView;

    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_list);

        filter = null;

        createBottomBarNavigation();

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        // load Video List
        createList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_top_videolist, menu);

        // Set an icon in the ActionBar
        menu.findItem(R.id.action_account).setIcon(
                new IconicsDrawable(this, FontAwesome.Icon.faw_user_circle).actionBar());

        menu.findItem(R.id.action_server_address_book).setIcon(
                new IconicsDrawable(this, FontAwesome.Icon.faw_server).actionBar());

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchMenuItem.setIcon(
                new IconicsDrawable(this, FontAwesome.Icon.faw_search).actionBar());

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setQueryRefinementEnabled(true);

        searchMenuItem.getActionView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(VideoListActivity.this)
                        .setTitle(getString(R.string.clear_search_history))
                        .setMessage(getString(R.string.clear_search_history_prompt))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getApplicationContext(),
                                        SearchSuggestionsProvider.AUTHORITY,
                                        SearchSuggestionsProvider.MODE);
                                suggestions.clearHistory();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });
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
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                String suggestion = getSuggestion(position);
                searchView.setQuery(suggestion, true);
                return true;
            }

            private String getSuggestion(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(
                        position);
                return cursor.getString(cursor
                        .getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                /* Required to implement */
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, VideoPlayerService.class));
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SWITCH_INSTANCE) {
            if(resultCode == RESULT_OK) {
                loadVideos(currentStart, count, sort, filter);
            }
        }
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
            case R.id.action_account:
//                if (!Session.getInstance().isLoggedIn()) {

                //Intent intentLogin = new Intent(this, ServerAddressBookActivity.class);

                    Intent intentMe = new Intent(this, MeActivity.class);
                    this.startActivity(intentMe);

                    //overridePendingTransition(R.anim.slide_in_bottom, 0);


                  //  this.startActivity(intentLogin);

//                } else {
//                    Intent intentMe = new Intent(this, MeActivity.class);
//                    this.startActivity(intentMe);
//                }
                return false;
            case R.id.action_server_address_book:
                Intent addressBookActivityIntent = new Intent(this, ServerAddressBookActivity.class);
                this.startActivityForResult(addressBookActivityIntent, SWITCH_INSTANCE);
                return false;
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
        Set<String> languages = sharedPref.getStringSet("pref_language", null);
        String apiBaseURL = APIUrlHelper.getUrlWithVersion(this);

        GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetVideoDataService.class);

        Call<VideoList> call;
        if (!searchQuery.equals("")) {
            call = service.searchVideosData(start, count, sort, nsfw, searchQuery, filter, languages);
        } else if (subscriptions) {
            GetUserService userService = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetUserService.class);
            call = userService.getVideosSubscripions(start, count, sort);
        } else {
            call = service.getVideosData(start, count, sort, nsfw, filter, languages);
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
                Toast.makeText(VideoListActivity.this, getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


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
        super.onNewIntent(intent);
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


    private void createBottomBarNavigation() {

        // Get Bottom Navigation
        BottomNavigationView navigation = findViewById(R.id.navigation);

        // Always show text label
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        // Add Icon font
        Menu navMenu = navigation.getMenu();
        navMenu.findItem(R.id.navigation_overview).setIcon(
                new IconicsDrawable(this, FontAwesome.Icon.faw_globe));
        navMenu.findItem(R.id.navigation_trending).setIcon(
                new IconicsDrawable(this, FontAwesome.Icon.faw_fire));
        navMenu.findItem(R.id.navigation_recent).setIcon(
                new IconicsDrawable(this, FontAwesome.Icon.faw_plus_circle));
        navMenu.findItem(R.id.navigation_local).setIcon(
                new IconicsDrawable(this, FontAwesome.Icon.faw_home));
        navMenu.findItem(R.id.navigation_subscriptions).setIcon(
                new IconicsDrawable(this, FontAwesome.Icon.faw_folder));
//        navMenu.findItem(R.id.navigation_account).setIcon(
//                new IconicsDrawable(this, FontAwesome.Icon.faw_user_circle));

        // Click Listener
        navigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_overview:
                    // TODO
                    if (!isLoading) {
                        sort = "-createdAt";
                        currentStart = 0;
                        filter = null;
                        subscriptions = false;
                        loadVideos(currentStart, count, sort, filter);
                    }

                    return true;
                case R.id.navigation_trending:
                    //Log.v(TAG, "navigation_trending");

                    if (!isLoading) {
                        sort = "-trending";
                        currentStart = 0;
                        filter = null;
                        subscriptions = false;
                        loadVideos(currentStart, count, sort, filter);
                    }

                    return true;
                case R.id.navigation_recent:
                    if (!isLoading) {
                        sort = "-createdAt";
                        currentStart = 0;
                        filter = null;
                        subscriptions = false;
                        loadVideos(currentStart, count, sort, filter);
                    }

                    return true;
                case R.id.navigation_local:
                    //Log.v(TAG, "navigation_trending");

                    if (!isLoading) {
                        sort = "-publishedAt";
                        filter = "local";
                        currentStart = 0;
                        subscriptions = false;
                        loadVideos(currentStart, count, sort, filter);
                    }

                    return true;
                case R.id.navigation_subscriptions:
                    //Log.v(TAG, "navigation_subscriptions");

                    if (!Session.getInstance().isLoggedIn()) {
//                        Intent intent = new Intent(this, LoginActivity.class);
//                        this.startActivity(intent);
                        Intent addressBookActivityIntent = new Intent(this, ServerAddressBookActivity.class);
                        this.startActivityForResult(addressBookActivityIntent, SWITCH_INSTANCE);
                        return false;
                    } else {

                        if (!isLoading) {
                            sort = "-publishedAt";
                            filter = null;
                            currentStart = 0;
                            subscriptions = true;
                            loadVideos(currentStart, count, sort, filter);
                        }
                        return true;
                    }


//                case R.id.navigation_account:
//                    //Log.v(TAG, "navigation_account");
//                    //Toast.makeText(VideoListActivity.this, "Account Not Implemented", Toast.LENGTH_SHORT).show();
//
//                    if (!Session.getInstance().isLoggedIn()) {
//                        Intent intent = new Intent(this, LoginActivity.class);
//                        this.startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(this, MeActivity.class);
//                        this.startActivity(intent);
//                    }
//
//                    return false;
            }
            return false;
        });

        // TODO: on double click jump to top and reload
//        navigation.setOnNavigationItemReselectedListener(menuItemReselected -> {
//            switch (menuItemReselected.getItemId()) {
//                case R.id.navigation_home:
//                    if (!isLoading) {
//                        sort = "-createdAt";
//                        currentStart = 0;
//                        filter = null;
//                        subscriptions = false;
//                        loadVideos(currentStart, count, sort, filter);
//                    }
//                case R.id.navigation_trending:
//                    if (!isLoading) {
//                        sort = "-trending";
//                        currentStart = 0;
//                        filter = null;
//                        subscriptions = false;
//                        loadVideos(currentStart, count, sort, filter);
//                    }
//                case R.id.navigation_local:
//                    if (!isLoading) {
//                        sort = "-publishedAt";
//                        filter = "local";
//                        currentStart = 0;
//                        subscriptions = false;
//                        loadVideos(currentStart, count, sort, filter);
//                    }
//                case R.id.navigation_subscriptions:
//                    if (Session.getInstance().isLoggedIn()) {
//                        if (!isLoading) {
//                            sort = "-publishedAt";
//                            filter = null;
//                            currentStart = 0;
//                            subscriptions = true;
//                            loadVideos(currentStart, count, sort, filter);
//                        }
//                    }
//            }
//        });

    }

}
