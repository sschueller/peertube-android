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
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
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


import java.util.ArrayList;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PAUSE;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PLAY;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_STOP;
import static net.schueller.peertube.helper.VideoHelper.canEnterPipMode;

public class VideoPlayActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayActivity";

    static boolean floatMode = false;

    private static final int REQUEST_CODE = 101;
    private BroadcastReceiver receiver;

    //This can only be called when in entering pip mode which can't happen if the device doesn't support pip mode.
    @SuppressLint("NewApi")
    public void makePipControls() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentById(R.id.video_player_fragment);

        ArrayList<RemoteAction> actions = new ArrayList<>();

        Intent actionIntent = new Intent(getString(R.string.app_background_audio));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, actionIntent, 0);
        @SuppressLint({"NewApi", "LocalSuppress"}) Icon icon = Icon.createWithResource(getApplicationContext(), android.R.drawable.stat_sys_speakerphone);
        @SuppressLint({"NewApi", "LocalSuppress"}) RemoteAction remoteAction = new RemoteAction(icon, "close pip", "from pip window custom command", pendingIntent);
        actions.add(remoteAction);

        actionIntent = new Intent(ACTION_STOP);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, actionIntent, 0);
        icon = Icon.createWithResource(getApplicationContext(), com.google.android.exoplayer2.ui.R.drawable.exo_notification_stop);
        remoteAction = new RemoteAction(icon, "play", "stop the media", pendingIntent);
        actions.add(remoteAction);

        assert videoPlayerFragment != null;
        if (videoPlayerFragment.isPaused()) {
            Log.e(TAG, "setting actions with play button");
            actionIntent = new Intent(ACTION_PLAY);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, actionIntent, 0);
            icon = Icon.createWithResource(getApplicationContext(), com.google.android.exoplayer2.ui.R.drawable.exo_notification_play);
            remoteAction = new RemoteAction(icon, "play", "play the media", pendingIntent);
        } else {
            Log.e(TAG, "setting actions with pause button");
            actionIntent = new Intent(ACTION_PAUSE);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, actionIntent, 0);
            icon = Icon.createWithResource(getApplicationContext(), com.google.android.exoplayer2.ui.R.drawable.exo_notification_pause);
            remoteAction = new RemoteAction(icon, "pause", "pause the media", pendingIntent);
        }
        actions.add(remoteAction);


        //add custom actions to pip window
        PictureInPictureParams params =
                new PictureInPictureParams.Builder()
                        .setActions(actions)
                        .build();
        setPictureInPictureParams(params);
    }

    public void changedToPipMode() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentById(R.id.video_player_fragment);

        assert videoPlayerFragment != null;
        videoPlayerFragment.showControls(false);
        //create custom actions
        makePipControls();

        //setup receiver to handle customer actions
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STOP);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_PLAY);
        filter.addAction((getString(R.string.app_background_audio)));
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                if (action.equals(ACTION_PAUSE)) {
                    videoPlayerFragment.pauseVideo();
                    makePipControls();
                }
                if (action.equals(ACTION_PLAY)) {
                    videoPlayerFragment.unPauseVideo();
                    makePipControls();
                }

                if (action.equals(getString(R.string.app_background_audio))) {
                    unregisterReceiver(receiver);
                    finish();
                }
                if (action.equals(ACTION_STOP)) {
                    unregisterReceiver(receiver);
                    finishAndRemoveTask();
                }
            }
        };
        registerReceiver(receiver, filter);

        Log.v(TAG, "switched to pip ");
        floatMode = true;
        videoPlayerFragment.showControls(false);
    }

    public void changedToNormalMode() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentById(R.id.video_player_fragment);

        assert videoPlayerFragment != null;
        videoPlayerFragment.showControls(true);
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        Log.v(TAG, "switched to normal");
        floatMode = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set theme
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(getResources().getIdentifier(
                sharedPref.getString(
                        getString(R.string.pref_theme_key),
                        getString(R.string.app_default_theme)
                ),
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
        Log.v(TAG, "oncreate click: " + videoUuid + " is trying to replace: " + playingVideo);

        if (TextUtils.isEmpty(playingVideo)) {
            Log.v(TAG, "oncreate no video currently playing");
            videoPlayerFragment.start(videoUuid);
        } else if (!playingVideo.equals(videoUuid)) {
            Log.v(TAG, "oncreate different video playing currently");
            videoPlayerFragment.stopVideo();
            videoPlayerFragment.start(videoUuid);
        } else {
            Log.v(TAG, "oncreate same video playing currently");
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
        Log.v(TAG, "new intent click: " + videoUuid + " is trying to replace: " + videoPlayerFragment.getVideoUuid());
        String playingVideo = videoPlayerFragment.getVideoUuid();

        if (TextUtils.isEmpty(playingVideo)) {
            Log.v(TAG, "new intent no video currently playing");
            videoPlayerFragment.start(videoUuid);
        } else if (!playingVideo.equals(videoUuid)) {
            Log.v(TAG, "new intent different video playing currently");
            videoPlayerFragment.stopVideo();
            videoPlayerFragment.start(videoUuid);
        } else {
            Log.v(TAG, "new intent same video playing currently");
        }

        // if we are in landscape set the video to fullscreen
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setOrientation(true);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
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

        assert videoPlayerFragment != null;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoPlayerFragment.requireView().getLayoutParams();
        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params.height = isLandscape ? FrameLayout.LayoutParams.MATCH_PARENT : (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());

        videoPlayerFragment.requireView().setLayoutParams(params);

        if (videoMetaFragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

            if (isLandscape) {
                transaction.hide(videoMetaFragment);
            } else {
                transaction.show(videoMetaFragment);
            }

            transaction.commit();
        }

        videoPlayerFragment.setIsFullscreen(isLandscape);

        if ( isLandscape ) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
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
    public void onUserLeaveHint() {

        Log.v(TAG, "onUserLeaveHint()...");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentById(R.id.video_player_fragment);
        VideoMetaDataFragment videoMetaDataFragment = (VideoMetaDataFragment) fragmentManager.findFragmentById(R.id.video_meta_data_fragment);

        String backgroundBehavior = sharedPref.getString(getString(R.string.pref_background_behavior_key), getString(R.string.pref_background_stop_key));

        assert videoPlayerFragment != null;
        assert backgroundBehavior != null;
        if ( videoMetaDataFragment.isLeaveAppExpected() )
        {
            super.onUserLeaveHint();
            return;
        }

        if (backgroundBehavior.equals(getString(R.string.pref_background_stop_key))) {
            Log.v(TAG, "stop the video");

            videoPlayerFragment.pauseVideo();
            stopService(new Intent(this, VideoPlayerService.class));
            super.onBackPressed();

        } else if (backgroundBehavior.equals(getString(R.string.pref_background_audio_key))) {
            Log.v(TAG, "play the Audio");
            super.onBackPressed();

        } else if (backgroundBehavior.equals(getString(R.string.pref_background_float_key))) {
            Log.v(TAG, "play in floating video");
            //canEnterPIPMode makes sure API level is high enough
            if (canEnterPipMode(this)) {
                Log.v(TAG, "enabling pip");
                enterPipMode();
            } else {
                Log.v(TAG, "unable to use pip");
            }

        } else {
            // Deal with bad entries from older version
            Log.v(TAG, "No setting, fallback");
            super.onBackPressed();

        }


    }

    // @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed()...");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment)
                getSupportFragmentManager().findFragmentById(R.id.video_player_fragment);

        assert videoPlayerFragment != null;

        // copying Youtube behavior to have back button exit full screen.
        if (videoPlayerFragment.getIsFullscreen()) {
            Log.v(TAG, "exiting full screen");
            videoPlayerFragment.fullScreenToggle();
            return;
        }
        // pause video if pref is enabled
        if (sharedPref.getBoolean(getString(R.string.pref_back_pause_key), true)) {
            videoPlayerFragment.pauseVideo();
        }

        String backgroundBehavior = sharedPref.getString(getString(R.string.pref_background_behavior_key), getString(R.string.pref_background_stop_key));

        assert backgroundBehavior != null;

        if (backgroundBehavior.equals(getString(R.string.pref_background_stop_key))) {
            Log.v(TAG, "stop the video");
            videoPlayerFragment.pauseVideo();
            stopService(new Intent(this, VideoPlayerService.class));
            super.onBackPressed();

        } else if (backgroundBehavior.equals(getString(R.string.pref_background_audio_key))) {
            Log.v(TAG, "play the Audio");
            super.onBackPressed();

        } else if (backgroundBehavior.equals(getString(R.string.pref_background_float_key))) {
            Log.v(TAG, "play in floating video");
            //canEnterPIPMode makes sure API level is high enough
            if (canEnterPipMode(this)) {
                Log.v(TAG, "enabling pip");
                enterPipMode();
                //fixes problem where back press doesn't bring up video list after returning from PIP mode
                Intent intentSettings = new Intent(this, VideoListActivity.class);
                this.startActivity(intentSettings);
            } else {
                Log.v(TAG, "Unable to enter PIP mode");
                super.onBackPressed();
            }

        } else {
            // Deal with bad entries from older version
            Log.v(TAG, "No setting, fallback");
            super.onBackPressed();

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enterPipMode() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentById( R.id.video_player_fragment );

        if ( videoPlayerFragment.getVideoAspectRatio() == 0 ) {
            Log.i( TAG, "impossible to switch to pip" );
        } else {
            Rational rational = new Rational( (int) ( videoPlayerFragment.getVideoAspectRatio() * 100 ), 100 );
            PictureInPictureParams mParams =
                    new PictureInPictureParams.Builder()
                            .setAspectRatio( rational )
//                          .setSourceRectHint(new Rect(0,500,400,600))
                            .build();

            enterPictureInPictureMode( mParams );
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentById(R.id.video_player_fragment);

        if (videoPlayerFragment != null) {

            if (isInPictureInPictureMode) {
                changedToPipMode();
                Log.v(TAG, "switched to pip ");
                videoPlayerFragment.useController(false);
            } else {
                changedToNormalMode();
                Log.v(TAG, "switched to normal");
                videoPlayerFragment.useController(true);
            }

        } else {
            Log.e(TAG, "videoPlayerFragment is NULL");
        }
    }

}
