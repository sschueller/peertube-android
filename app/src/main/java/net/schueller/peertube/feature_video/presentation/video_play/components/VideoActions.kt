package net.schueller.peertube.feature_video.presentation.video_play.components

import android.util.Log
import android.widget.ScrollView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.ContextUtils.getActivity
import net.schueller.peertube.R
import net.schueller.peertube.feature_server_address.domain.model.Server
import net.schueller.peertube.feature_server_address.presentation.address_add_edit.AddEditAddressEvent
import net.schueller.peertube.feature_video.domain.model.RATING_DISLIKE
import net.schueller.peertube.feature_video.domain.model.RATING_LIKE
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.presentation.video_play.VideoPlayEvent
import net.schueller.peertube.feature_video.presentation.video_play.VideoPlayViewModel


@Composable
fun VideoActions(
    video: Video,
    viewModel: VideoPlayViewModel = hiltViewModel()
) {

    val state = viewModel.state.value

    Row(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {

        val thumbsUpIcon = if (state.rating?.rating === RATING_LIKE) {
            R.drawable.ic_thumbs_up_filled
        } else {
            R.drawable.ic_thumbs_up
        }

        VideoAction(thumbsUpIcon, "Thumbs Up", video.likes.toString()) {
            viewModel.onEvent(VideoPlayEvent.UpVoteVideo(video))
        }

        val thumbsDownIcon = if (state.rating?.rating === RATING_DISLIKE) {
            R.drawable.ic_thumbs_down_filled
        } else {
            R.drawable.ic_thumbs_down
        }

        VideoAction(thumbsDownIcon, "Thumbs Down", video.dislikes.toString()) {
            viewModel.onEvent(VideoPlayEvent.DownVoteVideo(video))
        }

        VideoAction(R.drawable.ic_share_2, "Share", "Share") {
            viewModel.onEvent(VideoPlayEvent.ShareVideo(video))
        }

        if (video.downloadEnabled == true) {
            VideoAction(R.drawable.ic_download, "Download", "Download") {
                viewModel.onEvent(VideoPlayEvent.DownloadVideo(video))
            }
        }

        VideoAction(R.drawable.ic_playlist_add, "Add to Playlist", "Add") {
            viewModel.onEvent(VideoPlayEvent.AddVideoToPlaylist(video))
        }

        VideoAction(R.drawable.ic_slash, "Block", "Block") {
            viewModel.onEvent(VideoPlayEvent.BlockVideo(video))
        }

        VideoAction(R.drawable.ic_flag, "Flag", "Flag") {
            viewModel.onEvent(VideoPlayEvent.FlagVideo(video))
        }

    }
    
}

@Composable
fun VideoAction(
    icon: Int,
    iconText: String,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(
                top = 8.dp,
                bottom = 8.dp,
                start = 8.dp,
                end = 8.dp
            )
            .width(56.dp)
            .clickable(
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painterResource(id = icon),
            modifier = Modifier
                .size(28.dp)
                .padding(bottom = 4.dp),
            contentDescription = iconText
        )
        Text(
            text = text,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.caption
        )
    }
}