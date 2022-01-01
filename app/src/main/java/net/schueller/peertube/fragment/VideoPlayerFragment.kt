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
package net.schueller.peertube.fragment

import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.google.android.exoplayer2.ui.PlayerView
import android.content.Intent
import net.schueller.peertube.service.VideoPlayerService
import com.github.se_bastiaan.torrentstream.TorrentStream
import android.widget.LinearLayout
import android.view.GestureDetector
import android.content.ServiceConnection
import android.content.ComponentName
import android.os.IBinder
import net.schueller.peertube.service.VideoPlayerService.LocalBinder
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.AspectRatioListener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import net.schueller.peertube.R
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import android.widget.TextView
import android.widget.FrameLayout
import net.schueller.peertube.helper.APIUrlHelper
import net.schueller.peertube.network.GetVideoDataService
import net.schueller.peertube.network.RetrofitInstance
import android.widget.Toast
import net.schueller.peertube.helper.ErrorHelper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.StreamStatus
import com.google.android.exoplayer2.decoder.DecoderCounters
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.view.GestureDetector.SimpleOnGestureListener
import androidx.annotation.RequiresApi
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.github.se_bastiaan.torrentstream.TorrentOptions.Builder
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.util.Util
import com.mikepenz.iconics.Iconics
import net.schueller.peertube.R.layout
import net.schueller.peertube.R.string
import net.schueller.peertube.helper.VideoHelper
import net.schueller.peertube.model.Video
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.math.abs

class VideoPlayerFragment : Fragment(), VideoRendererEventListener {

    var videoUuid: String? = null
        private set
//    private var progressBar: ProgressBar? = null
    private var exoPlayer: PlayerView? = null
    private var videoPlayerIntent: Intent? = null
    private var mBound = false
    private var isFullscreen = false
    private var mService: VideoPlayerService? = null
    private var torrentStream: TorrentStream? = null
//    private var torrentStatus: LinearLayout? = null
    var videoAspectRatio = 0f
        private set
    private var mDetector: GestureDetector? = null
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")
            val binder = service as LocalBinder
            mService = binder.service

            // 2. Create the player
            exoPlayer!!.player = mService!!.player
            mBound = true
            loadVideo()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")
            exoPlayer!!.player = null
            mBound = false
        }
    }
    private val aspectRatioListener: AspectRatioListener = AspectRatioListener {
            targetAspectRatio, _, _ -> videoAspectRatio = targetAspectRatio
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(layout.fragment_video_player, container, false)
    }

    fun start(videoUuid: String?) {

        // start service
        val context = context
        val activity: Activity? = activity
        this.videoUuid = videoUuid
        assert(activity != null)
//        progressBar = activity?.findViewById(R.id.torrent_progress)
//        progressBar?.max = 100
        assert(context != null)
        exoPlayer = PlayerView(context!!)
        exoPlayer = activity?.findViewById(R.id.video_view)
        exoPlayer?.controllerShowTimeoutMs = 1000
        exoPlayer?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        mDetector = GestureDetector(context, MyGestureListener())
        exoPlayer?.setOnTouchListener(touchListener)
        exoPlayer?.setAspectRatioListener(aspectRatioListener)
//        torrentStatus = activity?.findViewById(R.id.exo_torrent_status)

        // Full screen Icon
        val fullscreenText = activity?.findViewById<TextView>(R.id.exo_fullscreen)
        val fullscreenButton = activity?.findViewById<FrameLayout>(R.id.exo_fullscreen_button)
        fullscreenText?.setText(string.video_expand_icon)
        if (fullscreenText != null) {
            Iconics.Builder().on(fullscreenText).build()
            fullscreenButton?.setOnClickListener {
                Log.d(TAG, "Fullscreen")
                fullScreenToggle()
            }
        }
        if (!mBound) {
            videoPlayerIntent = Intent(context, VideoPlayerService::class.java)
            activity?.bindService(videoPlayerIntent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun loadVideo() {
        val context = context

        // get video details from api
        val apiBaseURL = APIUrlHelper.getUrlWithVersion(context)
        val service =
            RetrofitInstance.getRetrofitInstance(apiBaseURL, APIUrlHelper.useInsecureConnection(context)).create(
                GetVideoDataService::class.java
            )
        val call = service.getVideoData(videoUuid)
        call.enqueue(object : Callback<Video?> {
            override fun onResponse(call: Call<Video?>, response: Response<Video?>) {
                val video = response.body()
                mService!!.setCurrentVideo(video)
                if (video == null) {
                    Toast.makeText(
                        context,
                        "Unable to retrieve video information, try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                playVideo(video)
            }

            override fun onFailure(call: Call<Video?>, t: Throwable) {
                Log.wtf(TAG, t.fillInStackTrace())
                ErrorHelper.showToastFromCommunicationError(activity, t)
            }
        })
    }

    fun useController(value: Boolean) {
        if (mBound) {
            exoPlayer!!.useController = value
        }
    }

    private fun playVideo(video: Video) {
        val context = context

        // video Meta fragment
        val videoMetaDataFragment =
            (requireActivity().supportFragmentManager.findFragmentById(R.id.video_meta_data_fragment) as VideoMetaDataFragment?)!!
        videoMetaDataFragment.updateVideoMeta(video, mService)

        val sharedPref = context?.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE
        )

        var prefTorrentPlayer = false
        var videoQuality = 999999
        if (sharedPref != null) {
            prefTorrentPlayer = sharedPref.getBoolean(getString(string.pref_torrent_player_key), false)
            videoQuality = sharedPref.getInt(getString(string.pref_quality_key), 999999)
        }

//        if (prefTorrentPlayer) {
//            torrentStatus!!.visibility = View.VISIBLE
//            val stream = video.files[0].torrentUrl
//            Log.v(TAG, "getTorrentUrl : " + video.files[0].torrentUrl)
//            torrentStream = setupTorrentStream()
//            torrentStream!!.startStream(stream)
//        } else {
            var urlToPlay: String? = null
            var isHLS = false

            // try HLS stream first
            // get video qualities
            // TODO: if auto is set all versions except 0p should be added to a track and have exoplayer auto select optimal bitrate
            if (video.streamingPlaylists.size > 0) {
                urlToPlay = video.streamingPlaylists[0].playlistUrl
                isHLS = true
            } else {
                if (video.files.size > 0) {
                    urlToPlay = video.files[0].fileUrl // default, take first found, usually highest res
                    for (file in video.files) {
                        // Set quality if it matches
                        if (file.resolution.id == videoQuality) {
                            urlToPlay = file.fileUrl
                        }
                    }
                }
            }
            if (urlToPlay!!.isNotEmpty()) {
                mService!!.setCurrentStreamUrl(urlToPlay, isHLS)
//                torrentStatus!!.visibility = View.GONE
                startPlayer()
            } else {
                stopVideo()
                Toast.makeText(context, string.api_error, Toast.LENGTH_LONG).show()
            }
//        }
        Log.v(TAG, "end of load Video")
    }

    private fun startPlayer() {
        Util.startForegroundService(requireContext(), videoPlayerIntent!!)
    }

    fun destroyVideo() {
        exoPlayer!!.player = null
        if (torrentStream != null) {
            torrentStream!!.stopStream()
        }
    }

    fun pauseVideo() {
        if (mBound) {
            mService!!.player!!.playWhenReady = false
        }
    }

//    fun pauseToggle() {
//        if (mBound) {
//            mService!!.player!!.playWhenReady = !mService!!.player!!.playWhenReady
//        }
//    }

    fun unPauseVideo() {
        if (mBound) {
            mService!!.player!!.playWhenReady = true
        }
    }

    val isPaused: Boolean
        get() = !mService!!.player!!.playWhenReady

    fun showControls(value: Boolean) {
        exoPlayer!!.useController = value
    }

    fun stopVideo() {
        if (mBound) {
            requireContext().unbindService(mConnection)
            mBound = false
        }
    }

    /**
     * triggered rotation and button press
     */
    fun setIsFullscreen(fullscreen: Boolean) {
        isFullscreen = fullscreen
        val fullscreenButton = requireActivity().findViewById<TextView>(R.id.exo_fullscreen)
        if (fullscreen) {
            hideSystemBars()
            fullscreenButton.setText(string.video_compress_icon)
        } else {
            restoreSystemBars()
            fullscreenButton.setText(string.video_expand_icon)
        }
        Iconics.Builder().on(fullscreenButton).build()
    }

    private fun hideSystemBars()
    {
        val view = this.view
        if (view != null) {
            val windowInsetsController =
                ViewCompat.getWindowInsetsController(view) ?: return
            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // Hide both the status bar and the navigation bar
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun restoreSystemBars()
    {
        val view = this.view
        if (view != null) {
            val windowInsetsController =
                ViewCompat.getWindowInsetsController(view) ?: return
            // Show both the status bar and the navigation bar
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun getIsFullscreen(): Boolean {
        return isFullscreen
    }

    /**
     * Triggered by button press
     */
    fun fullScreenToggle() {
        if (!isFullscreen) {
            setIsFullscreen(true)
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            setIsFullscreen(false)
            // we want to force portrait if fullscreen is switched of as we do not have a min. landscape view
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
//
//    /**
//     * Torrent Playback
//     *
//     * @return torrent stream
//     */
//    private fun setupTorrentStream(): TorrentStream {
//        val torrentOptions = Builder()
//            .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
//            .removeFilesAfterStop(true)
//            .build()
//        val torrentStream = TorrentStream.init(torrentOptions)
//        torrentStream.addListener(object : TorrentListener {
//            override fun onStreamReady(torrent: Torrent) {
//                val videoPath = Uri.fromFile(torrent.videoFile).toString()
//                Log.d(TAG, "Ready! torrentStream videoPath:$videoPath")
//                mService!!.setCurrentStreamUrl(videoPath, false)
//                startPlayer()
//            }
//
//            override fun onStreamProgress(torrent: Torrent, streamStatus: StreamStatus) {
//                if (streamStatus.bufferProgress <= 100 && progressBar!!.progress < 100 && progressBar!!.progress != streamStatus.bufferProgress) {
//                    //Log.d(TAG, "Progress: " + streamStatus.bufferProgress);
//                    progressBar!!.progress = streamStatus.bufferProgress
//                }
//            }
//
//            override fun onStreamStopped() {
//                Log.d(TAG, "Stopped")
//            }
//
//            override fun onStreamPrepared(torrent: Torrent) {
//                Log.d(TAG, "Prepared")
//            }
//
//            override fun onStreamStarted(torrent: Torrent) {
//                Log.d(TAG, "Started")
//            }
//
//            override fun onStreamError(torrent: Torrent, e: Exception) {
//                Log.d(TAG, "Error: " + e.message)
//            }
//        })
//        return torrentStream
//    }

    override fun onVideoEnabled(counters: DecoderCounters) {
        Log.v(TAG, "onVideoEnabled()...")
    }

    override fun onVideoDecoderInitialized(
        decoderName: String,
        initializedTimestampMs: Long,
        initializationDurationMs: Long
    ) {
    }

    override fun onVideoInputFormatChanged(format: Format) {}
    override fun onDroppedFrames(count: Int, elapsedMs: Long) {}
    override fun onVideoDisabled(counters: DecoderCounters) {
        Log.v(TAG, "onVideoDisabled()...")
    }

    // touch event on video player
    private var touchListener = OnTouchListener { _, event ->
        //v.performClick() // causes flicker but should be implemented for accessibility
        mDetector!!.onTouchEvent(event)
    }

    internal inner class MyGestureListener : SimpleOnGestureListener() {

        /*
                @Override
                public boolean onDown(MotionEvent event) {
                    Log.d("TAG","onDown: ");
                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    Log.i("TAG", "onSingleTapConfirmed: ");
                    pauseToggle();
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    Log.i("TAG", "onLongPress: ");
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.i("TAG", "onDoubleTap: ");
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                        float distanceX, float distanceY) {
                    Log.i("TAG", "onScroll: ");
                    return true;
                }
        */
        @RequiresApi(api = VERSION_CODES.N)
        override fun onFling(
            event1: MotionEvent, event2: MotionEvent,
            velocityX: Float, velocityY: Float
        ): Boolean {
            Log.d(TAG, event1.toString())
            Log.d(TAG, event2.toString())
            Log.d(TAG, velocityX.toString())
            Log.d(TAG, velocityY.toString())
            //arbitrarily velocity speeds that seem to work to differentiate events.
            if (velocityY > 4000) {
                Log.d(TAG, "we have a drag down event")
                if (VideoHelper.canEnterPipMode(context)) {
                    if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
                        val pipParams = PictureInPictureParams.Builder()
                        requireActivity().enterPictureInPictureMode(pipParams.build())
                    }
                }
            }
            if (velocityX > 2000 && abs(velocityY) < 2000) {
                Log.d(TAG, "swipe right $velocityY")
            }
            if (velocityX < 2000 && abs(velocityY) < 2000) {
                Log.d(TAG, "swipe left $velocityY")
            }
            return true
        }
    }

    companion object {

        private const val TAG = "VideoPlayerFragment"
    }
}