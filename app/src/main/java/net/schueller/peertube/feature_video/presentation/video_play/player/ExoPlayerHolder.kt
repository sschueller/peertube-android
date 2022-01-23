package net.schueller.peertube.feature_video.presentation.video_play.player

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.util.Util
import net.schueller.peertube.common.VideoHelper
import net.schueller.peertube.feature_video.domain.model.Video
import javax.inject.Singleton

@Singleton
object ExoPlayerHolder {
    private var exoplayer: ExoPlayer? = null
    private var currentVideo: Video? = null
    private val videoHelper = VideoHelper()

    fun setVideo(video: Video, context: Context): ExoPlayer {

        if (exoplayer == null) {
            exoplayer = createExoPlayer(context)
        }

        // check if its the same video
        if (currentVideo === null || currentVideo?.uuid !== video.uuid) {

            val videoUri = Uri.parse(videoHelper.pickPlaybackResolution(video))
            val dataSourceFactory = DataSourceHolder.getCacheFactory(context)
            val source = when (Util.inferContentType(videoUri)) {
                C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(videoUri))
                C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(videoUri))
                else -> ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(videoUri))
            }
            exoplayer!!.setMediaSource(source)
            exoplayer!!.prepare()
            exoplayer!!.playWhenReady = true

            currentVideo = video
        }

        return exoplayer!!
    }

    private fun createExoPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setLoadControl(
                DefaultLoadControl.Builder().setBufferDurationsMs(
                    DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS / 10,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS / 10
                ).build()
            )
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
            }
    }
}