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
package net.schueller.peertube.service

import android.app.Notification
import net.schueller.peertube.helper.MetaDataHelper.getMetaString
import net.schueller.peertube.model.Video.Companion.getMediaDescription
import android.os.IBinder
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import android.content.IntentFilter
import android.media.AudioManager
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import android.media.session.PlaybackState
import android.content.Intent
import android.widget.Toast
import net.schueller.peertube.helper.APIUrlHelper
import net.schueller.peertube.network.UnsafeOkHttpClient
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import android.app.PendingIntent
import android.app.Service
import net.schueller.peertube.activity.VideoPlayActivity
import net.schueller.peertube.activity.VideoListActivity
import android.graphics.Bitmap
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import android.support.v4.media.MediaDescriptionCompat
import android.content.BroadcastReceiver
import android.content.Context
import android.net.Uri
import android.os.Binder
import android.util.Log
import android.webkit.URLUtil
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import net.schueller.peertube.R.drawable
import net.schueller.peertube.R.string
import net.schueller.peertube.model.Video
import java.lang.Exception

class VideoPlayerService : Service() {

    private val mBinder: IBinder = LocalBinder()
    @JvmField
    var player: ExoPlayer? = null
    private var currentVideo: Video? = null
    private var currentStreamUrl: String? = null
    private var currentStreamUrlIsHLS = false
    private var playerNotificationManager: PlayerNotificationManager? = null
    private val becomeNoisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val myNoisyAudioStreamReceiver = BecomingNoisyReceiver()
    override fun onCreate() {
        Log.v(TAG, "onCreate...")
        super.onCreate()
        player = ExoPlayer.Builder(applicationContext)
            .setTrackSelector(DefaultTrackSelector(applicationContext))
            .build()

        // Stop player if audio device changes, e.g. headphones unplugged
        player!!.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState.toLong() == PlaybackState.ACTION_PAUSE) { // this means that pause is available, hence the audio is playing
                    Log.v(TAG, "ACTION_PLAY: $playbackState")
                    registerReceiver(myNoisyAudioStreamReceiver, becomeNoisyIntentFilter)
                }
                if (playbackState
                        .toLong() == PlaybackState.ACTION_PLAY
                ) { // this means that play is available, hence the audio is paused or stopped
                    Log.v(TAG, "ACTION_PAUSE: $playbackState")
                    safeUnregisterReceiver()
                }
            }
        })
    }

    inner class LocalBinder : Binder() {

        // Return this instance of VideoPlayerService so clients can call public methods
        val service: VideoPlayerService
            get() =// Return this instance of VideoPlayerService so clients can call public methods
                this@VideoPlayerService
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy...")
        if (playerNotificationManager != null) {
            playerNotificationManager!!.setPlayer(null)
        }
        //Was seeing an error when exiting the program about not unregistering the receiver.
        safeUnregisterReceiver()
        if (player != null) {
            player!!.release()
            player = null
        }
        super.onDestroy()
    }

    private fun safeUnregisterReceiver() {
        try {
            unregisterReceiver(myNoisyAudioStreamReceiver)
        } catch (e: Exception) {
            Log.e("VideoPlayerService", "attempted to unregister a non-registered service")
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val context: Context = this
        Log.v(TAG, "onStartCommand...")
        return if (!URLUtil.isValidUrl(currentStreamUrl)) {
            Toast.makeText(context, "Invalid URL provided. Unable to play video.", Toast.LENGTH_SHORT).show()
            START_NOT_STICKY
        } else {
            playVideo()
            START_STICKY
        }
    }

    fun setCurrentVideo(video: Video?) {
        Log.v(TAG, "setCurrentVideo...")
        currentVideo = video
    }

    fun setCurrentStreamUrl(streamUrl: String, isHLS: Boolean) {
        Log.v(TAG, "setCurrentStreamUrl...$streamUrl")
        currentStreamUrlIsHLS = isHLS
        currentStreamUrl = streamUrl
    }

    /**
     * Returns the current playback speed of the player.
     *
     * @return the current playback speed of the player.
     *///Playback speed control
    var playBackSpeed: Float
        get() = player!!.playbackParameters.speed
        set(speed) {
            Log.v(TAG, "setPlayBackSpeed...")
            player!!.playbackParameters = PlaybackParameters(speed)
        }

    private fun playVideo() {
        val context: Context = this

        // We need a valid URL
        Log.v(TAG, "playVideo...")

        // Produces DataSource instances through which media data is loaded.
        val okhttpClientBuilder: okhttp3.OkHttpClient.Builder = if (!APIUrlHelper.useInsecureConnection(this)) {
            okhttp3.OkHttpClient.Builder()
        } else {
            UnsafeOkHttpClient.getUnsafeOkHttpClientBuilder()
        }

        // Create a data source factory.
        val dataSourceFactory:  OkHttpDataSource.Factory =  OkHttpDataSource.Factory(
            okhttpClientBuilder.build()
        )

        // Create a progressive media source pointing to a stream uri.
        val mediaSource: MediaSource = if (currentStreamUrlIsHLS) {
            HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(currentStreamUrl)))
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(currentStreamUrl)))
        }

        // Set the media source to be played.
        player!!.setMediaSource(mediaSource)

        // Prepare the player.
        player!!.prepare()

        // Auto play
        player!!.playWhenReady = true

        //set playback speed to global default
        val sharedPref = getSharedPreferences(
            packageName + "_preferences",
            Context.MODE_PRIVATE
        )

        val speed = sharedPref.getString(getString(string.pref_video_speed_key), "1.0")!!.toFloat()
        playBackSpeed = speed

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            PLAYBACK_NOTIFICATION_ID,
            PLAYBACK_CHANNEL_ID,
        ).setMediaDescriptionAdapter(
                object : MediaDescriptionAdapter {
                    override fun getCurrentContentTitle(player: Player): CharSequence {
                        return currentVideo!!.name
                    }

                    override fun createCurrentContentIntent(player: Player): PendingIntent? {
                        val intent = Intent(context, VideoPlayActivity::class.java)
                        intent.putExtra(VideoListActivity.EXTRA_VIDEOID, currentVideo!!.uuid)
                        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    }

                    override fun getCurrentContentText(player: Player): CharSequence {
                        return getMetaString(
                                currentVideo!!.createdAt,
                                currentVideo!!.views,
                                baseContext
                        )
                    }

                    override fun getCurrentSubText(player: Player): CharSequence { return ""}

                    override fun getCurrentLargeIcon(
                            player: Player,
                            callback: PlayerNotificationManager.BitmapCallback
                    ): Bitmap? {
                        return null
                    }
                }
        ).setNotificationListener(
            object : NotificationListener {

                override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
                    super.onNotificationPosted(notificationId, notification, ongoing)
                    if (ongoing) // allow notification to be dismissed if player is stopped
                        startForeground(notificationId, notification)
                    else
                        stopForeground(false)
                }

                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                    super.onNotificationCancelled(notificationId, dismissedByUser)
                    stopSelf()
                    stopForeground(true)
                }
            }
        ).setChannelNameResourceId(string.playback_channel_name)
        .setChannelDescriptionResourceId(string.playback_channel_description)
        .build()

        playerNotificationManager!!.setPriority(NotificationCompat.PRIORITY_DEFAULT)

        playerNotificationManager!!.setSmallIcon(drawable.ic_logo_bw)

        // don't show skip buttons in notification
        playerNotificationManager!!.setUseNextAction(false)
        playerNotificationManager!!.setUsePreviousAction(false)

        playerNotificationManager!!.setPlayer(player)

        // external Media control, Android Wear / Google Home etc.
        val mediaSession = MediaSessionCompat(context, MEDIA_SESSION_TAG)
        mediaSession.isActive = true
        playerNotificationManager!!.setMediaSessionToken(mediaSession.sessionToken)
        val mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
            override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
                return getMediaDescription(currentVideo!!)
            }
        })
        mediaSessionConnector.setPlayer(player)

        // Audio Focus
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MOVIE)
            .build()
        player!!.setAudioAttributes(audioAttributes, true)
    }

    // pause playback on audio output change
    private inner class BecomingNoisyReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                player!!.playWhenReady = false
            }
        }
    }

    companion object {

        private const val TAG = "VideoPlayerService"
        private const val MEDIA_SESSION_TAG = "peertube_player"
        private const val PLAYBACK_CHANNEL_ID = "playback_channel"
        private const val PLAYBACK_NOTIFICATION_ID = 1
    }
}