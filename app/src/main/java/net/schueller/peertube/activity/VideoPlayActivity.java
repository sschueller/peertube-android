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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.util.Log;
import android.util.TypedValue;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.mikepenz.iconics.Iconics;
import com.squareup.picasso.Picasso;
import net.schueller.peertube.R;
import net.schueller.peertube.fragment.VideoMetaDataFragment;
import net.schueller.peertube.fragment.VideoOptionsFragment;
import net.schueller.peertube.fragment.VideoPlayerFragment;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.helper.MetaDataHelper;
import net.schueller.peertube.intents.Intents;
import net.schueller.peertube.model.Account;
import net.schueller.peertube.model.Avatar;
import net.schueller.peertube.model.Video;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.service.VideoPlayerService;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.schueller.peertube.helper.Constants.BACKGROUND_PLAY_PREF_KEY;
import static net.schueller.peertube.helper.Constants.DEFAULT_THEME;
import static net.schueller.peertube.helper.Constants.THEME_PREF_KEY;

public class VideoPlayActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set theme
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(getResources().getIdentifier(
                sharedPref.getString(THEME_PREF_KEY, DEFAULT_THEME),
                "style",
                getPackageName())
        );

        setContentView(R.layout.activity_video_play);

        // get video ID
        Intent intent = getIntent();
        String videoUuid = intent.getStringExtra(VideoListActivity.EXTRA_VIDEOID);
        Log.v(TAG, "click: " + videoUuid);

        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment)
                getSupportFragmentManager().findFragmentById(R.id.video_player_fragment);

        assert videoPlayerFragment != null;
        videoPlayerFragment.start(videoUuid);

    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.v(TAG, "onConfigurationChanged()...");

        super.onConfigurationChanged(newConfig);


        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment videoPlayerFragment = fragmentManager.findFragmentById(R.id.video_player_fragment);

//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
//        params.weight = 3.0f;
//        fragment.getView().setLayoutParams(params);

        // Checking the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoPlayerFragment.getView().getLayoutParams();
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            //simpleExoPlayerView.setLayoutParams(params);

            videoPlayerFragment.getView().setLayoutParams(params);

            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(fragmentManager.findFragmentById(R.id.video_meta_data_fragment))
                    .commit();

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoPlayerFragment.getView().getLayoutParams();
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
            //simpleExoPlayerView.setLayoutParams(params);

            videoPlayerFragment.getView().setLayoutParams(params);

            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(fragmentManager.findFragmentById(R.id.video_meta_data_fragment))
                    .commit();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


    @Override
    protected void onDestroy() {

        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment)
                getSupportFragmentManager().findFragmentById(R.id.video_player_fragment);

        assert videoPlayerFragment != null;
        videoPlayerFragment.destroyVideo();


        super.onDestroy();
        Log.v(TAG, "onDestroy...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onStop() {
        super.onStop();

//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//
//        Log.v(TAG, "" + sharedPref.getBoolean(BACKGROUND_PLAY_PREF_KEY, false));
//
//        if (!sharedPref.getBoolean(BACKGROUND_PLAY_PREF_KEY, false)) {
//            Log.v(TAG, "BACKGROUND_PLAY_PREF_KEY...");
//            stopService(new Intent(this, VideoPlayerService.class));
//        }

        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment)
                getSupportFragmentManager().findFragmentById(R.id.video_player_fragment);

        assert videoPlayerFragment != null;
        videoPlayerFragment.stopVideo();

        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.v(TAG, "onStart()...");
    }

}
