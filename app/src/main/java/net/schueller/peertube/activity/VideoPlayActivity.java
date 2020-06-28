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


import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.util.TypedValue;

import android.view.WindowManager;
import android.widget.FrameLayout;

import android.widget.RelativeLayout;

import net.schueller.peertube.R;
import net.schueller.peertube.fragment.VideoMetaDataFragment;
import net.schueller.peertube.fragment.VideoPlayerFragment;
import net.schueller.peertube.service.VideoPlayerService;

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
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment)
                getSupportFragmentManager().findFragmentById(R.id.video_player_fragment);

        assert videoPlayerFragment != null;
        String playingVideo = videoPlayerFragment.getVideoUuid();
        Log.v(TAG, "oncreate click: " + videoUuid +" is trying to replace: "+playingVideo);

        if (TextUtils.isEmpty(playingVideo)){
            Log.v(TAG,"oncreate no video currently playing");
            videoPlayerFragment.start(videoUuid);
        } else if(!playingVideo.equals(videoUuid)){
            Log.v(TAG,"oncreate different video playing currently");
            videoPlayerFragment.stopVideo();
            videoPlayerFragment.start(videoUuid);
        } else {
            Log.v(TAG,"oncreate same video playing currently");
        }

        // if we are in landscape set the video to fullscreen
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setOrientation(true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment)
                getSupportFragmentManager().findFragmentById(R.id.video_player_fragment);
        assert videoPlayerFragment != null;
        String videoUuid = intent.getStringExtra(VideoListActivity.EXTRA_VIDEOID);
        Log.v(TAG, "new intent click: " + videoUuid +" is trying to replace: "+videoPlayerFragment.getVideoUuid());
        assert videoPlayerFragment != null;
        String playingVideo = videoPlayerFragment.getVideoUuid();

        if (TextUtils.isEmpty(playingVideo)){
            Log.v(TAG,"new intent no video currently playing");
            videoPlayerFragment.start(videoUuid);
        } else if(!playingVideo.equals(videoUuid)){
            Log.v(TAG,"new intent different video playing currently");
            videoPlayerFragment.stopVideo();
            videoPlayerFragment.start(videoUuid);
        } else {
            Log.v(TAG,"new intent same video playing currently");
        }

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

    @SuppressLint("NewApi")
    @Override
    public void onUserLeaveHint () {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentById(R.id.video_player_fragment);
        VideoMetaDataFragment videoMetaFragment = (VideoMetaDataFragment) fragmentManager.findFragmentById(R.id.video_meta_data_fragment);
        String backgroundBehavior = sharedPref.getString("pref_background_behavior","backgroundStop");

        switch(backgroundBehavior){
            case "backgroundStop":
                Log.v(TAG,"stop the video");
                videoPlayerFragment.pauseVideo();
                stopService(new Intent(this, VideoPlayerService.class));
                super.onBackPressed();
                break;
            case "backgroundAudio":
                Log.v(TAG,"play the Audio");
                super.onBackPressed();
                break;
            case "backgroundFloat":
                Log.v(TAG,"play in floating video");
                //canEnterPIPMode makes sure API level is high enough
                if (canEnterPipMode(this)) {
                    Log.v(TAG, "enabling pip");
                    enterPipMode();
                } else {
                    Log.v(TAG, "unable to use pip");
                }
                break;
        }
        Log.v(TAG, "onUserLeaveHint()...");
    }

   // @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    public void onBackPressed() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment)
                getSupportFragmentManager().findFragmentById(R.id.video_player_fragment);

        //copying Youtube behavior to have back button exit full screen.
        if (videoPlayerFragment.getIsFullscreen()){
            Log.v(TAG,"exiting full screen");
            videoPlayerFragment.fullScreenToggle();
            return;
        }

        if (sharedPref.getBoolean("pref_back_pause", true)) {
            assert videoPlayerFragment != null;
            videoPlayerFragment.pauseVideo();
        }

        String backgroundBehavior = sharedPref.getString("pref_background_behavior","backgroundStop");
        switch (backgroundBehavior){
            case "backgroundStop":
                Log.v(TAG,"stop the video");
                videoPlayerFragment.pauseVideo();
                stopService(new Intent(this, VideoPlayerService.class));
                super.onBackPressed();
                break;
            case "backgroundAudio":
               Log.v(TAG,"play the Audio");
               super.onBackPressed();
               break;
            case "backgroundFloat":
                Log.v(TAG,"play in floating video");
                //canEnterPIPMode makes sure API level is high enough
                if (canEnterPipMode(this)) {
                    Log.v(TAG, "enabling pip");
                    enterPipMode();
                    //fixes problem where back press doesn't bring up video list after returning from PIP mode
                    Intent intentSettings = new Intent(this, VideoListActivity.class);
                    this.startActivity(intentSettings);
                } else {
                    Log.v(TAG,"Unable to enter PIP mode");
                    super.onBackPressed();
                }
                break;
        }
        Log.v(TAG, "onBackPressed()...");
    }
    public boolean canEnterPipMode(Context context) {
        Log.v(TAG,"api version "+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT<28){
            return false;
        }
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        return (AppOpsManager.MODE_ALLOWED== appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), context.getPackageName()));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enterPipMode() {
        Rational rational = new Rational(239, 100);
        Log.v(TAG,rational.toString());
        PictureInPictureParams mParams =
                new PictureInPictureParams.Builder()
                        .setAspectRatio(rational)
//                        .setSourceRectHint(new Rect(0,500,400,600))
                        .build();

        enterPictureInPictureMode(mParams);
    }
    @Override
    public void onPictureInPictureModeChanged (boolean isInPictureInPictureMode, Configuration newConfig) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentById(R.id.video_player_fragment);

        if (isInPictureInPictureMode) {
            Log.v(TAG,"switched to pip ");
            videoPlayerFragment.useController(false);
        } else {
            Log.v(TAG,"switched to normal");
            videoPlayerFragment.useController(true);
        }
    }

}
