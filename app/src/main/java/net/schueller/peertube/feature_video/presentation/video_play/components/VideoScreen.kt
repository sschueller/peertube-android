package net.schueller.peertube.feature_video.presentation.video_play.components

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.PlayerView
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.presentation.video_play.player.ExoPlayerHolder
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import net.schueller.peertube.feature_video.presentation.video_play.player.PlayerViewPool

@Composable
fun VideoScreen(
    exoPlayerHolder: ExoPlayerHolder,
    video: Video
) {
    val context = LocalContext.current
    val player = exoPlayerHolder.setVideo(video, context)

    AndroidView(
        modifier = Modifier
            .fillMaxWidth(),


//                modifier = Modifier.aspectRatio(video.width.toFloat() / video.height.toFloat()),
        factory = { context ->
            val frameLayout = FrameLayout(context)
//                    frameLayout.setBackgroundColor(context.getColor(android.R.color.holo_blue_bright))
            frameLayout
        },
        update = { frameLayout ->
            frameLayout.removeAllViews()

                val playerView = PlayerViewPool.get(frameLayout.context)
                playerView.player = player

                PlayerView.switchTargetView(
                    player,
                    PlayerViewPool.currentPlayerView,
                    playerView
                )
                PlayerViewPool.currentPlayerView = playerView
                frameLayout.addView(
                    playerView,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )

        }
    )

}