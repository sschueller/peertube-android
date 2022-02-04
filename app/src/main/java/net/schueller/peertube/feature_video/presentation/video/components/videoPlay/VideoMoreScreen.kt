package net.schueller.peertube.feature_video.presentation.video.components.videoPlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.schueller.peertube.feature_video.presentation.video.VideoPlayViewModel


@Composable
fun VideoMoreScreen(
    viewModel: VideoPlayViewModel = hiltViewModel()
) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {

                }
            ) {
                Icon(
                    Icons.Filled.Settings,
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .padding(8.dp),
                    contentDescription = "Quality",
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Quality",
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {

                }
            ) {
                Icon(
                    Icons.Filled.Settings,
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .padding(8.dp),
                    contentDescription = "Captions",
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Captions",
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {

                }
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .padding(8.dp),
                    contentDescription = "Playback Speed",
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Playback Speed",
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }

}
