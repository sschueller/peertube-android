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
import net.schueller.peertube.fragment.VideoPlayerFragment;

import java.util.Objects;

import androidx.fragment.app.Fragment;
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

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.v(TAG, "onConfigurationChanged()...");

        super.onConfigurationChanged(newConfig);


        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment videoPlayerFragment = fragmentManager.findFragmentById(R.id.video_player_fragment);
        Fragment videoMetaFragment = fragmentManager.findFragmentById(R.id.video_meta_data_fragment);

        // Checking the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
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

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

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
