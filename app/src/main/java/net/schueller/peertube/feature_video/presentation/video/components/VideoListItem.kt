package net.schueller.peertube.feature_video.presentation.video.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.presentation.common.*

@ExperimentalCoilApi
@Composable
fun VideoListItem(
    video: Video,
    onItemClick: (Video) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onItemClick(video) },
        shape = RectangleShape,
    ) {
        Column (
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxWidth(),
//                .clickable { onItemClick(video) }
        ) {
            Box() {
                val image = rememberImagePainter(
                    data = getImageUrl(video.previewPath),
//                    builder = {
//                        placeholder(R.drawable.test_image)
//                    }
                )
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .placeholder(
                            visible = (image.state is ImagePainter.State.Error || image.state is ImagePainter.State.Empty),
                            highlight = PlaceholderHighlight.fade()
                        ),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(
                            Alignment.BottomEnd
                        )
                        .padding(2.dp)
                ) {
                    VideoTime(video)
                }
            }

            // Video Meta
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .height(84.dp) // TODO: not setting this causes odd up scroll effect
            ) {
                val avatar = rememberImagePainter(
                    data  = getCreatorAvatarUrl(getCreatorAvatar(video))
                )
                Image(
                    painter = avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .height(72.dp)
                        .width(72.dp)
                        .padding(12.dp)
                        .clip(shape = RoundedCornerShape(100.dp))
                        .placeholder(
                            visible = (avatar.state is ImagePainter.State.Error || avatar.state is ImagePainter.State.Empty),
                            highlight = if (avatar.state is ImagePainter.State.Empty) {
                                PlaceholderHighlight.fade()
                            } else {
                                null
                            },
                        ),
                    contentScale = ContentScale.Crop
                )
                Column (
                    modifier = Modifier
                        .padding(6.dp),
                )
                {
                    Text(
                        text = video.name ?: "No Name",
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text =  getMetaDataTag(video.createdAt, video.views, "",true),
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        text =  getCreatorString(video, true),
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.caption
                    )
                }
            }

        }

    }
}
