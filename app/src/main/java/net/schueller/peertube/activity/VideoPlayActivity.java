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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.TypedValue;

import android.view.WindowManager;
import android.widget.FrameLayout;

import android.widget.RelativeLayout;

import net.schueller.peertube.R;
import net.schueller.peertube.fragment.VideoMetaDataFragment;
import net.schueller.peertube.fragment.VideoPlayerFragment;

import java.util.Objects;

import androidx.fragment.app.FragmentManager;


//import static net.schueller.peertube.helper.Constants.BACKGROUND_PLAY_PREF_KEY;
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

        // if we are in landscape set the video to fullscreen
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setOrientation(true);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.v(TAG, "onConfigurationChanged()...");

        super.onConfigurationChanged(newConfig);

        // Checking the orientation changes of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setOrientation(true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setOrientation(false);
        }
    }



    private void setOrientation(Boolean isLandscape) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentById(R.id.video_player_fragment);
        VideoMetaDataFragment videoMetaFragment = (VideoMetaDataFragment) fragmentManager.findFragmentById(R.id.video_meta_data_fragment);

        if (isLandscape) {
            assert videoPlayerFragment != null;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Objects.requireNonNull(videoPlayerFragment.getView()).getLayoutParams();
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            videoPlayerFragment.getView().setLayoutParams(params);

            if (videoMetaFragment != null) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .hide(videoMetaFragment)
                        .commit();
            }
            videoPlayerFragment.setIsFullscreen(true);

        } else {
            assert videoPlayerFragment != null;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Objects.requireNonNull(videoPlayerFragment.getView()).getLayoutParams();
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
            videoPlayerFragment.getView().setLayoutParams(params);

            if (videoMetaFragment != null) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .show(videoMetaFragment)
                        .commit();
            }
            videoPlayerFragment.setIsFullscreen(false);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    public void onBackPressed() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("pref_back_pause", true)) {
            VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.video_player_fragment);
            assert videoPlayerFragment != null;
            videoPlayerFragment.pauseVideo();
        }
        Log.v(TAG, "onBackPressed()...");
        super.onBackPressed();
    }
}
