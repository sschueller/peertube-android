package net.schueller.peertube.feature_video.presentation.video.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.schueller.peertube.R
import net.schueller.peertube.feature_video.domain.model.Video


@Composable
fun VideoTime(
    video: Video
) {
    val backgroundColor = if (video.isLive) {
        Color(
            red = 0xFF,
            blue = 0,
            green = 0,
            alpha = 0xCC
        )
    } else {
        Color(
            red = 0,
            blue = 0,
            green = 0,
            alpha = 0x99
        )
    }

    val timeStamp = if (video.isLive) {
        "LIVE"
    } else {
        getDuration(video.duration.toLong())
    }

    Box(
        modifier = Modifier
            .wrapContentHeight()
            .background(
                color = backgroundColor
            )
    ) {
        Row() {
            if (video.isLive) {
                Icon(
                    painterResource(id = R.drawable.ic_radio),
                    contentDescription = "signupAllowed",
                    modifier = Modifier.requiredSize(18.dp)
                        .align(CenterVertically)
                        .padding(
                        start = 4.dp
                    ),
                    tint = Color(
                        red = 0xFF,
                        blue = 0xFF,
                        green = 0xFF,
                        alpha = 0xFF
                    )
                )
            }
            Text(
                modifier = Modifier.padding(
                    end = 4.dp,
                    start = 4.dp
                ),
                color = Color(
                    red = 0xFF,
                    blue = 0xFF,
                    green = 0xFF,
                    alpha = 0xFF
                ),
                text = timeStamp,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun getDuration(duration: Long?): String {
    return DateUtils.formatElapsedTime(duration!!)
}
