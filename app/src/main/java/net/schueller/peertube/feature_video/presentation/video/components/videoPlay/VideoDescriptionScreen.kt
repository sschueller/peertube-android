package net.schueller.peertube.feature_video.presentation.video.components.videoPlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.schueller.peertube.feature_video.presentation.video.events.VideoPlayEvent
import net.schueller.peertube.feature_video.presentation.video.VideoPlayViewModel

@Composable
fun VideoDescriptionScreen(
    viewModel: VideoPlayViewModel = hiltViewModel()
) {

    val state = viewModel.stateVideoDescription.value

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
    ) {

        state.description?.let { description ->
            Column() {
                Button(
                    onClick = {
                        viewModel.onEvent(VideoPlayEvent.CloseDescription)
                    }
                ) {

                }
                Text(
                    text = description.description ?: "",
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }

        }
    }
}