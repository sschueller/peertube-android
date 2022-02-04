package net.schueller.peertube.feature_video.presentation.video.components.videoPlay

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.PlayerView
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.presentation.video.player.ExoPlayerHolder
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import net.schueller.peertube.R
import net.schueller.peertube.feature_video.presentation.video.events.VideoPlayEvent
import net.schueller.peertube.feature_video.presentation.video.VideoPlayViewModel
import net.schueller.peertube.feature_video.presentation.video.player.PlayerViewPool

@Composable
fun VideoScreen(
    exoPlayerHolder: ExoPlayerHolder,
    video: Video,
    modifier: Modifier,
    viewModel: VideoPlayViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val player = exoPlayerHolder.setVideo(video, context)

    val activity = LocalContext.current as Activity

    val configuration = LocalConfiguration.current

    AndroidView(
        modifier = modifier
            .fillMaxWidth(),
//                modifier = Modifier.aspectRatio(video.width.toFloat() / video.height.toFloat()),
        factory = { fContext ->
            val frameLayout = FrameLayout(fContext)
            frameLayout
        },
        update = { frameLayout ->

            Log.v("VideoScreen", "Android view update")

            frameLayout.removeAllViews()

            Log.v("VideoScreen", "frameLayout removal")


            val playerView = PlayerViewPool.get(frameLayout.context)
            playerView.player = player

            Log.v("VideoScreen", "playerView assign")


            PlayerView.switchTargetView(
                player,
                PlayerViewPool.currentPlayerView,
                playerView
            )

            Log.v("VideoScreen", "switchTargetView")


            PlayerViewPool.currentPlayerView = playerView
            frameLayout.addView(
                playerView,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

            Log.v("VideoScreen", "currentPlayerView")


            // Video More Button
            val videoMoreButton = playerView.findViewById<FrameLayout>(R.id.exo_more_button)
            videoMoreButton.setOnClickListener {
                viewModel.onEvent(VideoPlayEvent.MoreButton)
            }

            Log.v("VideoScreen", "videoMoreButton")


            // TODO: does not update on orientation gesture
            val enterFullscreenIcon = playerView.findViewById<ImageButton>(R.id.exo_fullscreen_enable)
            val exitFullscreenIcon = playerView.findViewById<ImageButton>(R.id.exo_fullscreen_disable)

            if (configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                enterFullscreenIcon.visibility = View.GONE
                exitFullscreenIcon.visibility = View.VISIBLE
            } else {
                enterFullscreenIcon.visibility = View.VISIBLE
                exitFullscreenIcon.visibility = View.GONE
            }


            // TODO: locks out orientation gesture
            val fullscreenButton = playerView.findViewById<FrameLayout>(R.id.exo_fullscreen_button)
//            fullscreenButton.setOnClickListener {
//                if (configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
//                    Log.v("VP", "Go full screen")
//                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//                } else {
//                    Log.v("VP", "Exit full screen")
//                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                }
//            }


            Log.v("VideoScreen", "end")


        }
    )

}
