/*
 * Copyright (C) 2020 Stefan Schüller <sschueller@techdroid.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.activity

import android.Manifest.permission
import android.R.drawable
import android.R.string
import android.app.AlertDialog.Builder
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnSuggestionListener
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome.Icon.faw_fire
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome.Icon.faw_folder
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome.Icon.faw_globe
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome.Icon.faw_home
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome.Icon.faw_plus_circle
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome.Icon.faw_search
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome.Icon.faw_server
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome.Icon.faw_user_circle
import com.mikepenz.iconics.utils.actionBar
import net.schueller.peertube.R
import net.schueller.peertube.R.id
import net.schueller.peertube.R.layout
import net.schueller.peertube.adapter.VideoAdapter
import net.schueller.peertube.helper.APIUrlHelper
import net.schueller.peertube.helper.ErrorHelper
import net.schueller.peertube.model.VideoList
import net.schueller.peertube.network.GetUserService
import net.schueller.peertube.network.GetVideoDataService
import net.schueller.peertube.network.RetrofitInstance
import net.schueller.peertube.network.Session
import net.schueller.peertube.provider.SearchSuggestionsProvider
import net.schueller.peertube.service.VideoPlayerService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

private const val TAG = "_VideoListActivity"

class VideoListActivity : CommonActivity() {

    private var videoAdapter: VideoAdapter? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var currentStart = 0
    private val count = 12
    private var sort = "-createdAt"
    private var filter: String? = null
    private var searchQuery = ""
    private var subscriptions = false
    private var emptyView: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_video_list)
        filter = null
        createBottomBarNavigation()

        // Attaching the layout to the toolbar object
        val toolbar = findViewById<Toolbar>(id.tool_bar)
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar)

        // load Video List
        createList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_top_videolist, menu)

        // Set an icon in the ActionBar
        menu.findItem(id.action_account).icon = IconicsDrawable(this, faw_user_circle).actionBar()
        menu.findItem(id.action_server_address_book).icon = IconicsDrawable(this, faw_server).actionBar()
        val searchMenuItem = menu.findItem(id.action_search)
        searchMenuItem.icon = IconicsDrawable(this, faw_search).actionBar()

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = searchMenuItem.actionView as SearchView

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        searchView.isQueryRefinementEnabled = true
        searchMenuItem.actionView.setOnLongClickListener {
            Builder(this@VideoListActivity)
                .setTitle(getString(R.string.clear_search_history))
                .setMessage(getString(R.string.clear_search_history_prompt))
                .setPositiveButton(string.yes) { _, _ ->
                    val suggestions = SearchRecentSuggestions(
                        applicationContext,
                        SearchSuggestionsProvider.AUTHORITY,
                        SearchSuggestionsProvider.MODE
                    )
                    suggestions.clearHistory()
                }
                .setNegativeButton(string.no, null)
                .setIcon(drawable.ic_dialog_alert)
                .show()
            true
        }
        searchMenuItem.setOnActionExpandListener(object : OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                searchQuery = ""
                Log.d(TAG, "onMenuItemActionCollapse: ")
                loadVideos(0, count, sort, filter)
                return true
            }
        })

        // TODO, this doesn't work
        searchManager.setOnDismissListener {
            searchQuery = ""
            Log.d(TAG, "onDismiss: ")
            loadVideos(0, count, sort, filter)
        }
        searchView.setOnSuggestionListener(object : OnSuggestionListener {
            override fun onSuggestionClick(position: Int): Boolean {
                val suggestion = getSuggestion(position)
                searchView.setQuery(suggestion, true)
                return true
            }

            private fun getSuggestion(position: Int): String {
                val cursor = searchView.suggestionsAdapter.getItem(
                    position
                ) as Cursor
                return cursor.getString(
                    cursor
                        .getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
                )
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                /* Required to implement */
                return true
            }
        })
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, VideoPlayerService::class.java))
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SWITCH_INSTANCE) {
            if (resultCode == RESULT_OK) {
                loadVideos(currentStart, count, sort, filter)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            id.action_search ->                 //Toast.makeText(this, "Search Selected", Toast.LENGTH_SHORT).show();
                return false
            id.action_account -> {
                //                if (!Session.getInstance().isLoggedIn()) {

                //Intent intentLogin = new Intent(this, ServerAddressBookActivity.class);
                val intentMe = Intent(this, MeActivity::class.java)
                this.startActivity(intentMe)

                //overridePendingTransition(R.anim.slide_in_bottom, 0);

                //  this.startActivity(intentLogin);

//                } else {
//                    Intent intentMe = new Intent(this, MeActivity.class);
//                    this.startActivity(intentMe);
//                }
                return false
            }
            id.action_server_address_book -> {
                val addressBookActivityIntent = Intent(this, ServerAddressBookActivity::class.java)
                this.startActivityForResult(addressBookActivityIntent, SWITCH_INSTANCE)
                return false
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createList() {
        recyclerView = findViewById(id.recyclerView)
        swipeRefreshLayout = findViewById(id.swipeRefreshLayout)
        emptyView = findViewById(id.empty_view)
        val layoutManager: LayoutManager = LinearLayoutManager(this@VideoListActivity)
        recyclerView?.layoutManager = layoutManager
        videoAdapter = VideoAdapter(ArrayList(), this@VideoListActivity)
        recyclerView?.adapter = videoAdapter
        loadVideos(currentStart, count, sort, filter)
        recyclerView?.addOnScrollListener(object : OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    // is at end of list?
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (!isLoading) {
                            currentStart += count
                            loadVideos(currentStart, count, sort, filter)
                        }
                    }
                }
            }
        })
        swipeRefreshLayout?.setOnRefreshListener {
            // Refresh items
            if (!isLoading) {
                currentStart = 0
                loadVideos(currentStart, count, sort, filter)
            }
        }
    }

    private fun loadVideos(start: Int, count: Int, sort: String, filter: String?) {
        isLoading = true

        val sharedPref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        val nsfw = if (sharedPref.getBoolean(getString(R.string.pref_show_nsfw_key), false)) "both" else "false"

//
//        Locale locale = getResources().getConfiguration().locale;
//        String country = locale.getLanguage();
//
//        HashSet<String> countries = new HashSet<>(1);
//        countries.add(country);

        // We set this to default to null so that on initial start there are videos listed.
        val languages = sharedPref.getStringSet(getString(R.string.pref_video_language_key), null)
        val apiBaseURL = APIUrlHelper.getUrlWithVersion(this)
        val service =
            RetrofitInstance.getRetrofitInstance(apiBaseURL, APIUrlHelper.useInsecureConnection(this)).create(
                GetVideoDataService::class.java
            )
        val call: Call<VideoList> = when {
            searchQuery != "" -> {
                service.searchVideosData(start, count, sort, nsfw, searchQuery, filter, languages)
            }
            subscriptions -> {
                val userService =
                    RetrofitInstance.getRetrofitInstance(apiBaseURL, APIUrlHelper.useInsecureConnection(this)).create(
                        GetUserService::class.java
                    )
                userService.getVideosSubscripions(start, count, sort)
            }
            else -> {
                service.getVideosData(start, count, sort, nsfw, filter, languages)
            }
        }

        /*Log the URL called*/Log.d("URL Called", call.request().url.toString() + "")
        //        Toast.makeText(VideoListActivity.this, "URL Called: " + call.request().url(), Toast.LENGTH_SHORT).show();
        call.enqueue(object : Callback<VideoList?> {
            override fun onResponse(call: Call<VideoList?>, response: Response<VideoList?>) {
                if (currentStart == 0) {
                    videoAdapter!!.clearData()
                }
                if (response.body() != null) {
                    val videoList = response.body()!!.videoArrayList
                    if (videoList != null) {
                        videoAdapter!!.setData(response.body()!!.videoArrayList)
                    }
                }

                // no results show no results message
                if (currentStart == 0 && videoAdapter!!.itemCount == 0) {
                    emptyView!!.visibility = View.VISIBLE
                    recyclerView!!.visibility = View.GONE
                } else {
                    emptyView!!.visibility = View.GONE
                    recyclerView!!.visibility = View.VISIBLE
                }
                isLoading = false
                swipeRefreshLayout!!.isRefreshing = false
            }

            override fun onFailure(call: Call<VideoList?>, t: Throwable) {
                Log.wtf("err", t.fillInStackTrace())
                ErrorHelper.showToastFromCommunicationError(this@VideoListActivity, t)
                isLoading = false
                swipeRefreshLayout!!.isRefreshing = false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // only check when we actually need the permission
        val sharedPref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            sharedPref.getBoolean(getString(R.string.pref_torrent_player_key), false)
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            val suggestions = SearchRecentSuggestions(
                this,
                SearchSuggestionsProvider.AUTHORITY,
                SearchSuggestionsProvider.MODE
            )

            // Save recent searches
            suggestions.saveRecentQuery(query, null)
            if (query != null) {
                searchQuery = query
            }
            loadVideos(0, count, sort, filter)
        }
    }

    override fun onSearchRequested(): Boolean {
        val appData = Bundle()
        startSearch(null, false, appData, false)
        return true
    }

    private fun createBottomBarNavigation() {

        // Get Bottom Navigation
        val navigation = findViewById<BottomNavigationView>(id.navigation)

        // Always show text label
        navigation.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        // Add Icon font
        val navMenu = navigation.menu
        navMenu.findItem(id.navigation_overview).icon = IconicsDrawable(this, faw_globe)
        navMenu.findItem(id.navigation_trending).icon = IconicsDrawable(this, faw_fire)
        navMenu.findItem(id.navigation_recent).icon = IconicsDrawable(this, faw_plus_circle)
        navMenu.findItem(id.navigation_local).icon = IconicsDrawable(this, faw_home)
        navMenu.findItem(id.navigation_subscriptions).icon = IconicsDrawable(this, faw_folder)
        //        navMenu.findItem(R.id.navigation_account).setIcon(
//                new IconicsDrawable(this, FontAwesome.Icon.faw_user_circle));

        // Click Listener
        navigation.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                id.navigation_overview -> {
                    // TODO
                    if (!isLoading) {
                        sort = "-createdAt"
                        currentStart = 0
                        filter = null
                        subscriptions = false
                        loadVideos(currentStart, count, sort, filter)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                id.navigation_trending -> {
                    //Log.v(TAG, "navigation_trending");
                    if (!isLoading) {
                        sort = "-trending"
                        currentStart = 0
                        filter = null
                        subscriptions = false
                        loadVideos(currentStart, count, sort, filter)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                id.navigation_recent -> {
                    if (!isLoading) {
                        sort = "-createdAt"
                        currentStart = 0
                        filter = null
                        subscriptions = false
                        loadVideos(currentStart, count, sort, filter)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                id.navigation_local -> {
                    //Log.v(TAG, "navigation_trending");
                    if (!isLoading) {
                        sort = "-publishedAt"
                        filter = "local"
                        currentStart = 0
                        subscriptions = false
                        loadVideos(currentStart, count, sort, filter)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                id.navigation_subscriptions ->                     //Log.v(TAG, "navigation_subscriptions");
                    if (!Session.getInstance().isLoggedIn) {
//                        Intent intent = new Intent(this, LoginActivity.class);
//                        this.startActivity(intent);
                        val addressBookActivityIntent = Intent(this, ServerAddressBookActivity::class.java)
                        this.startActivityForResult(addressBookActivityIntent, SWITCH_INSTANCE)
                        return@setOnNavigationItemSelectedListener false
                    } else {
                        if (!isLoading) {
                            sort = "-publishedAt"
                            filter = null
                            currentStart = 0
                            subscriptions = true
                            loadVideos(currentStart, count, sort, filter)
                        }
                        return@setOnNavigationItemSelectedListener true
                    }
            }
            false
        }

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

    companion object {

        const val EXTRA_VIDEOID = "VIDEOID"
        const val EXTRA_ACCOUNTDISPLAYNAME = "ACCOUNTDISPLAYNAMEANDHOST"
        const val SWITCH_INSTANCE = 2
    }
}