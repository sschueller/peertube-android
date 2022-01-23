package net.schueller.peertube.feature_video.presentation.video_play.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import net.schueller.peertube.R
import net.schueller.peertube.common.Constants
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.presentation.common.getCreatorAvatar
import net.schueller.peertube.feature_video.presentation.common.getCreatorString
import net.schueller.peertube.feature_video.presentation.common.getMetaDataTag
import net.schueller.peertube.feature_video.presentation.video_play.VideoPlayEvent
import net.schueller.peertube.feature_video.presentation.video_play.VideoPlayViewModel

@ExperimentalCoilApi
@Composable
fun VideoMeta(
    video: Video,
    viewModel: VideoPlayViewModel = hiltViewModel()
) {
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colors.background
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        viewModel.onEvent(VideoPlayEvent.OpenDescription(video))
                    },
                )
        ) {
            Text(
                modifier = Modifier.padding(6.dp),
                text = video.name ?: "",
//            fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )
            Icon(
                painterResource(id = R.drawable.ic_chevron_down),
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 6.dp),
//                    .padding(end = 12.dp),
                contentDescription = "Open Description"
            )
        }
        Text(
            modifier = Modifier.padding(start = 6.dp, end = 6.dp),
            text =  getMetaDataTag(video.createdAt, video.views, "",true),
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.caption
        )

        VideoActions(video)

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .padding(
                    top = 2.dp,
                    bottom = 2.dp
                )
                .background(MaterialTheme.colors.onBackground)
        )
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
//                .padding(end = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(56.dp),
            ) {
                val avatar = rememberImagePainter(
                    data = Constants.BASE_IMAGE_URL + getCreatorAvatar(video)?.path
                )
                Image(
                    painter = avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .padding(8.dp)
                        .clip(shape = CircleShape)
                        .placeholder(
                            visible = (avatar.state is ImagePainter.State.Error || avatar.state is ImagePainter.State.Empty),
                            highlight = if (avatar.state is ImagePainter.State.Empty) {
                                PlaceholderHighlight.fade()
                            } else {
                                null
                            },
                        ),
//                    contentScale = ContentScale.Crop
                )
                Column(
//                    modifier = Modifier
//                        .padding(8.dp),
                )
                {
                    Text(
                        text = video.channel?.name ?: video.account?.name ?: "",
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = getCreatorString(video, true),
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
            Text(
                modifier = Modifier
                    .padding(end = 16.dp),
                text =  "SUBSCRIBE",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.button
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .padding(
                    top = 2.dp,
                    bottom = 2.dp
                )
                .background(MaterialTheme.colors.onBackground)
        )
        
    }
    
}