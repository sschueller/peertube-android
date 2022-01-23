package net.schueller.peertube.feature_video.presentation.me.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import net.schueller.peertube.R
import net.schueller.peertube.feature_video.domain.model.Avatar
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.presentation.common.getCreatorAvatarUrl

@ExperimentalCoilApi
@Composable
fun MeAvatar(
    avatar: Avatar?,
    onItemClick: () -> Unit
) {
    if (avatar?.path?.isNotEmpty() == true) {
        IconButton(onClick = onItemClick) {
            val avatarIcon = rememberImagePainter(
                data = getCreatorAvatarUrl(avatar)
            )
            Image(
                painter = avatarIcon,
                contentDescription = null,
                modifier = Modifier
                    .height(48.dp)
                    .width(48.dp)
                    .padding(8.dp)
                    .clip(shape = CircleShape)
                    .placeholder(
                        visible = (avatarIcon.state is ImagePainter.State.Error || avatarIcon.state is ImagePainter.State.Empty),
                        highlight = if (avatarIcon.state is ImagePainter.State.Empty) {
                            PlaceholderHighlight.fade()
                        } else {
                            null
                        },
                    ),
            )

        }
    } else {
        IconButton(onClick = onItemClick) {
            Icon(
                painterResource(id = R.drawable.ic_user),
                contentDescription = "Profile"
            )
        }
    }
}