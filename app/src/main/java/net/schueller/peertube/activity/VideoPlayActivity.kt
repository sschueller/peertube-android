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

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import net.schueller.peertube.fragment.VideoPlayerFragment
import net.schueller.peertube.R
import android.app.RemoteAction
import android.app.PendingIntent
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import android.app.PictureInPictureParams
import android.content.*
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Bundle
import android.text.TextUtils
import net.schueller.peertube.fragment.VideoMetaDataFragment
import android.widget.RelativeLayout
import android.widget.FrameLayout
import android.util.TypedValue
import android.view.WindowManager
import net.schueller.peertube.service.VideoPlayerService
import net.schueller.peertube.helper.VideoHelper
import androidx.annotation.RequiresApi
import android.os.Build
import android.util.Log
import android.util.Rational
import androidx.fragment.app.Fragment
import net.schueller.peertube.fragment.VideoDescriptionFragment
import java.util.ArrayList

class VideoPlayActivity : CommonActivity() {
    private var receiver: BroadcastReceiver? = null

    //This can only be called when in entering pip mode which can't happen if the device doesn't support pip mode.
    @SuppressLint("NewApi")
    fun makePipControls() {
        val fragmentManager = supportFragmentManager
        val videoPlayerFragment =
            fragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?
        val actions = ArrayList<RemoteAction>()
        var actionIntent = Intent(getString(R.string.app_background_audio))
        var pendingIntent =
            PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, actionIntent, 0)
        @SuppressLint("NewApi", "LocalSuppress") var icon = Icon.createWithResource(
            applicationContext, android.R.drawable.stat_sys_speakerphone
        )
        @SuppressLint("NewApi", "LocalSuppress") var remoteAction =
            RemoteAction(icon!!, "close pip", "from pip window custom command", pendingIntent!!)
        actions.add(remoteAction)
        actionIntent = Intent(PlayerNotificationManager.ACTION_STOP)
        pendingIntent =
            PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, actionIntent, 0)
        icon = Icon.createWithResource(
            applicationContext,
            com.google.android.exoplayer2.ui.R.drawable.exo_notification_stop
        )
        remoteAction = RemoteAction(icon, "play", "stop the media", pendingIntent)
        actions.add(remoteAction)
        assert(videoPlayerFragment != null)
        if (videoPlayerFragment!!.isPaused) {
            Log.e(TAG, "setting actions with play button")
            actionIntent = Intent(PlayerNotificationManager.ACTION_PLAY)
            pendingIntent =
                PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, actionIntent, 0)
            icon = Icon.createWithResource(
                applicationContext,
                com.google.android.exoplayer2.ui.R.drawable.exo_notification_play
            )
            remoteAction = RemoteAction(icon, "play", "play the media", pendingIntent)
        } else {
            Log.e(TAG, "setting actions with pause button")
            actionIntent = Intent(PlayerNotificationManager.ACTION_PAUSE)
            pendingIntent =
                PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, actionIntent, 0)
            icon = Icon.createWithResource(
                applicationContext,
                com.google.android.exoplayer2.ui.R.drawable.exo_notification_pause
            )
            remoteAction = RemoteAction(icon, "pause", "pause the media", pendingIntent)
        }
        actions.add(remoteAction)


        //add custom actions to pip window
        val params = PictureInPictureParams.Builder()
            .setActions(actions)
            .build()
        setPictureInPictureParams(params)
    }

    private fun changedToPipMode() {
        val fragmentManager = supportFragmentManager
        val videoPlayerFragment =
            (fragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?)!!
        videoPlayerFragment.showControls(false)
        //create custom actions
        makePipControls()

        //setup receiver to handle customer actions
        val filter = IntentFilter()
        filter.addAction(PlayerNotificationManager.ACTION_STOP)
        filter.addAction(PlayerNotificationManager.ACTION_PAUSE)
        filter.addAction(PlayerNotificationManager.ACTION_PLAY)
        filter.addAction(getString(R.string.app_background_audio))
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action!!
                if (action == PlayerNotificationManager.ACTION_PAUSE) {
                    videoPlayerFragment.pauseVideo()
                    makePipControls()
                }
                if (action == PlayerNotificationManager.ACTION_PLAY) {
                    videoPlayerFragment.unPauseVideo()
                    makePipControls()
                }
                if (action == getString(R.string.app_background_audio)) {
                    unregisterReceiver(receiver)
                    finish()
                }
                if (action == PlayerNotificationManager.ACTION_STOP) {
                    unregisterReceiver(receiver)
                    finishAndRemoveTask()
                }
            }
        }
        registerReceiver(receiver, filter)
        Log.v(TAG, "switched to pip ")
        floatMode = true
        videoPlayerFragment.showControls(false)
    }

    private fun changedToNormalMode() {
        val fragmentManager = supportFragmentManager
        val videoPlayerFragment =
            (fragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?)!!
        videoPlayerFragment.showControls(true)
        if (receiver != null) {
            unregisterReceiver(receiver)
        }
        Log.v(TAG, "switched to normal")
        floatMode = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set theme
        val sharedPref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        setTheme(
            resources.getIdentifier(
                sharedPref.getString(
                    getString(R.string.pref_theme_key),
                    getString(R.string.app_default_theme)
                ),
                "style",
                packageName
            )
        )
        setContentView(R.layout.activity_video_play)

        // get video ID
        val intent = intent
        val videoUuid = intent.getStringExtra(VideoListActivity.EXTRA_VIDEOID)
        val videoPlayerFragment =
            (supportFragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?)!!
        val playingVideo = videoPlayerFragment.videoUuid
        Log.v(TAG, "oncreate click: $videoUuid is trying to replace: $playingVideo")
        when {
            TextUtils.isEmpty(playingVideo) -> {
                Log.v(TAG, "oncreate no video currently playing")
                videoPlayerFragment.start(videoUuid)
            }
            playingVideo != videoUuid -> {
                Log.v(TAG, "oncreate different video playing currently")
                videoPlayerFragment.stopVideo()
                videoPlayerFragment.start(videoUuid)
            }
            else -> {
                Log.v(TAG, "oncreate same video playing currently")
            }
        }

        // if we are in landscape set the video to fullscreen
        val orientation = this.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setOrientation(true)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val videoPlayerFragment =
            (supportFragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?)!!
        val videoUuid = intent.getStringExtra(VideoListActivity.EXTRA_VIDEOID)
        Log.v(
            TAG,
            "new intent click: " + videoUuid + " is trying to replace: " + videoPlayerFragment.videoUuid
        )
        val playingVideo = videoPlayerFragment.videoUuid
        when {
            TextUtils.isEmpty(playingVideo) -> {
                Log.v(TAG, "new intent no video currently playing")
                videoPlayerFragment.start(videoUuid)
            }
            playingVideo != videoUuid -> {
                Log.v(TAG, "new intent different video playing currently")
                videoPlayerFragment.stopVideo()
                videoPlayerFragment.start(videoUuid)
            }
            else -> {
                Log.v(TAG, "new intent same video playing currently")
            }
        }

        // if we are in landscape set the video to fullscreen
        val orientation = this.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setOrientation(true)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.v(TAG, "onConfigurationChanged()...")
        super.onConfigurationChanged(newConfig)

        // Checking the orientation changes of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setOrientation(true)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setOrientation(false)
        }
    }

    private fun setOrientation(isLandscape: Boolean) {
        val fragmentManager = supportFragmentManager
        val videoPlayerFragment =
            fragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?
        val videoMetaFragment =
            fragmentManager.findFragmentById(R.id.video_meta_data_fragment) as VideoMetaDataFragment?
        assert(videoPlayerFragment != null)
        val params = videoPlayerFragment!!.requireView().layoutParams as RelativeLayout.LayoutParams
        params.width = FrameLayout.LayoutParams.MATCH_PARENT
        params.height =
            if (isLandscape) FrameLayout.LayoutParams.MATCH_PARENT else TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                250f,
                resources.displayMetrics
            )
                .toInt()
        videoPlayerFragment.requireView().layoutParams = params
        if (videoMetaFragment != null) {
            val transaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            if (isLandscape) {
                transaction.hide(videoMetaFragment)
            } else {
                transaction.show(videoMetaFragment)
            }
            transaction.commit()
        }
        videoPlayerFragment.setIsFullscreen(isLandscape)
        if (isLandscape) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    override fun onDestroy() {
        val videoPlayerFragment =
            (supportFragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?)!!
        videoPlayerFragment.destroyVideo()
        super.onDestroy()
        Log.v(TAG, "onDestroy...")
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause()...")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume()...")
    }

    override fun onStop() {
        super.onStop()
        val videoPlayerFragment =
            (supportFragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?)!!
        videoPlayerFragment.stopVideo()

        // TODO: doesn't remove fragment??
        val fragment: Fragment?  = supportFragmentManager.findFragmentByTag(VideoDescriptionFragment.TAG)
        if (fragment != null) {
            Log.v(TAG, "remove VideoDescriptionFragment")
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        Log.v(TAG, "onStop()...")
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart()...")
    }

    @SuppressLint("NewApi")
    public override fun onUserLeaveHint() {
        Log.v(TAG, "onUserLeaveHint()...")
        val sharedPref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        val fragmentManager = supportFragmentManager
        val videoPlayerFragment =
            fragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?
        val videoMetaDataFragment =
            fragmentManager.findFragmentById(R.id.video_meta_data_fragment) as VideoMetaDataFragment?
        val backgroundBehavior = sharedPref.getString(
            getString(R.string.pref_background_behavior_key),
            getString(R.string.pref_background_stop_key)
        )
        assert(videoPlayerFragment != null)
        assert(backgroundBehavior != null)
        if (videoMetaDataFragment!!.isLeaveAppExpected) {
            super.onUserLeaveHint()
            return
        }
        if (backgroundBehavior == getString(R.string.pref_background_stop_key)) {
            Log.v(TAG, "stop the video")
            videoPlayerFragment!!.pauseVideo()
            stopService(Intent(this, VideoPlayerService::class.java))
            super.onBackPressed()
        } else if (backgroundBehavior == getString(R.string.pref_background_audio_key)) {
            Log.v(TAG, "play the Audio")
            super.onBackPressed()
        } else if (backgroundBehavior == getString(R.string.pref_background_float_key)) {
            Log.v(TAG, "play in floating video")
            //canEnterPIPMode makes sure API level is high enough
            if (VideoHelper.canEnterPipMode(this)) {
                Log.v(TAG, "enabling pip")
                enterPipMode()
            } else {
                Log.v(TAG, "unable to use pip")
            }
        } else {
            // Deal with bad entries from older version
            Log.v(TAG, "No setting, fallback")
            super.onBackPressed()
        }
    }

    // @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    override fun onBackPressed() {
        Log.v(TAG, "onBackPressed()...")
        val sharedPref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        val videoPlayerFragment =
            (supportFragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?)!!

        // copying Youtube behavior to have back button exit full screen.
        if (videoPlayerFragment.getIsFullscreen()) {
            Log.v(TAG, "exiting full screen")
            videoPlayerFragment.fullScreenToggle()
            return
        }
        // pause video if pref is enabled
        if (sharedPref.getBoolean(getString(R.string.pref_back_pause_key), true)) {
            videoPlayerFragment.pauseVideo()
        }
        val backgroundBehavior = sharedPref.getString(
            getString(R.string.pref_background_behavior_key),
            getString(R.string.pref_background_stop_key)
        )!!
        if (backgroundBehavior == getString(R.string.pref_background_stop_key)) {
            Log.v(TAG, "stop the video")
            videoPlayerFragment.pauseVideo()
            stopService(Intent(this, VideoPlayerService::class.java))
            super.onBackPressed()
        } else if (backgroundBehavior == getString(R.string.pref_background_audio_key)) {
            Log.v(TAG, "play the Audio")
            super.onBackPressed()
        } else if (backgroundBehavior == getString(R.string.pref_background_float_key)) {
            Log.v(TAG, "play in floating video")
            //canEnterPIPMode makes sure API level is high enough
            if (VideoHelper.canEnterPipMode(this)) {
                Log.v(TAG, "enabling pip")
                enterPipMode()
                //fixes problem where back press doesn't bring up video list after returning from PIP mode
                val intentSettings = Intent(this, VideoListActivity::class.java)
                this.startActivity(intentSettings)
            } else {
                Log.v(TAG, "Unable to enter PIP mode")
                super.onBackPressed()
            }
        } else {
            // Deal with bad entries from older version
            Log.v(TAG, "No setting, fallback")
            super.onBackPressed()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun enterPipMode() {
        val fragmentManager = supportFragmentManager
        val videoPlayerFragment =
            fragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?
        if (videoPlayerFragment!!.videoAspectRatio == 0.toFloat()) {
            Log.i(TAG, "impossible to switch to pip")
        } else {
            val rational = Rational((videoPlayerFragment.videoAspectRatio * 100).toInt(), 100)
            val mParams = PictureInPictureParams.Builder()
                .setAspectRatio(rational) //                          .setSourceRectHint(new Rect(0,500,400,600))
                .build()
            enterPictureInPictureMode(mParams)
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        val fragmentManager = supportFragmentManager
        val videoPlayerFragment =
            fragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?
        if (videoPlayerFragment != null) {
            if (isInPictureInPictureMode) {
                changedToPipMode()
                Log.v(TAG, "switched to pip ")
                videoPlayerFragment.useController(false)
            } else {
                changedToNormalMode()
                Log.v(TAG, "switched to normal")
                videoPlayerFragment.useController(true)
            }
        } else {
            Log.e(TAG, "videoPlayerFragment is NULL")
        }
    }

    companion object {
        private const val TAG = "VideoPlayActivity"
        var floatMode = false
        private const val REQUEST_CODE = 101
    }
}