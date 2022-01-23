//package net.schueller.peertube.feature_video.presentation.video_play.components
//
//import android.app.Activity
//import android.net.Uri
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.viewinterop.AndroidView
//import com.google.android.exoplayer2.C
//import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.source.ProgressiveMediaSource
//import com.google.android.exoplayer2.source.dash.DashMediaSource
//import com.google.android.exoplayer2.source.hls.HlsMediaSource
//import com.google.android.exoplayer2.ui.PlayerView
//import com.google.android.exoplayer2.util.Util
//import net.schueller.peertube.common.VideoHelper
//import net.schueller.peertube.feature_video.domain.model.Video
//import net.schueller.peertube.feature_video.presentation.video_play.player.DataSourceHolder
//import net.schueller.peertube.feature_video.presentation.video_play.player.PlayerViewPool
//import net.schueller.peertube.feature_video.presentation.video_play.player.ExoPlayerHolder
//
//
//@Composable
//fun VideoItem(
//    video: Video,
//    modifier: Modifier
//) {
//    Surface(
//        modifier = modifier
//    ) {
//        val videoHelper = VideoHelper()
//
//        val context = LocalContext.current
//        val activity = context as Activity
//        val exoPlayer = remember { ExoPlayerHolder.get(context) }
//        var playerView: PlayerView? = null
//
//        LaunchedEffect(videoHelper.pickPlaybackResolution(video)) {
//            val videoUri = Uri.parse(videoHelper.pickPlaybackResolution(video))
//            val dataSourceFactory = DataSourceHolder.getCacheFactory(context)
//            val source = when (Util.inferContentType(videoUri)) {
//                C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(MediaItem.fromUri(videoUri))
//                C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(MediaItem.fromUri(videoUri))
//                else -> ProgressiveMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(MediaItem.fromUri(videoUri))
//            }
//            exoPlayer.setMediaSource(source)
//            exoPlayer.prepare()
//        }
//
//
//        AndroidView(
////                modifier = Modifier.aspectRatio(video.width.toFloat() / video.height.toFloat()),
//            factory = { context ->
//                val frameLayout = FrameLayout(context)
////                    frameLayout.setBackgroundColor(context.getColor(android.R.color.holo_blue_bright))
//                frameLayout
//            },
//            update = { frameLayout ->
//                frameLayout.removeAllViews()
//
//                    playerView = PlayerViewPool.get(frameLayout.context)
//                    PlayerView.switchTargetView(
//                        exoPlayer,
//                        PlayerViewPool.currentPlayerView,
//                        playerView
//                    )
//                    PlayerViewPool.currentPlayerView = playerView
//                    playerView!!.apply {
//                        player!!.playWhenReady = true
//                    }
//
//                    playerView?.apply {
//                        (parent as? ViewGroup)?.removeView(this)
//                    }
//                    frameLayout.addView(
//                        playerView,
//                        FrameLayout.LayoutParams.MATCH_PARENT,
//                        FrameLayout.LayoutParams.MATCH_PARENT
//                    )
////
////                    playerView?.apply {
////                        (parent as? ViewGroup)?.removeView(this)
////                        PlayerViewPool.release(this)
////                    }
////                    playerView = null
//
//            }
//        )
//
//        DisposableEffect(key1 = videoHelper.pickPlaybackResolution(video)) {
//            onDispose {
//                playerView?.apply {
//                    (parent as? ViewGroup)?.removeView(this)
//                }
//                exoPlayer.stop()
//                playerView?.let {
//                    PlayerViewPool.release(it)
//                }
//                playerView = null
//
//            }
//        }
//
//    }
//}
//
//
