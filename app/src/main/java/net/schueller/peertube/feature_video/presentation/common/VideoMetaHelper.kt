package net.schueller.peertube.feature_video.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import net.schueller.peertube.R
import net.schueller.peertube.common.Constants
import net.schueller.peertube.feature_video.domain.model.Account
import net.schueller.peertube.feature_video.domain.model.Avatar
import net.schueller.peertube.feature_video.domain.model.Video
import org.ocpsoft.prettytime.PrettyTime
import java.util.*


@Composable
fun getMetaDataTag(getCreatedAt: Date?, viewCount: Int, ownerString: String, reversed: Boolean = false): String {

    if (getCreatedAt !== null) {

        val context = LocalContext.current

        // Compatible with SDK 21+
        val currentLanguage = Locale.getDefault().displayLanguage
        val p = PrettyTime(currentLanguage)
        val relativeTime = p.format(Date(getCreatedAt.time))
        return if (reversed) {
            viewCount.toString() +
                    context.resources.getString(R.string.meta_data_views) +
                    context.resources.getString(R.string.meta_data_seperator) +
                    relativeTime
        } else {
            relativeTime +
                    context.resources.getString(R.string.meta_data_seperator) +
                    viewCount + context.resources.getString(R.string.meta_data_views)
        }
    } else {
        return ""
    }

}

@Composable
fun getTagsString(video: Video): String {
    return if (video.tags != null) {
        " #" + video.tags.joinToString(" #", "", "", 3, "")
    } else {
        " "
    }
}

@Composable
fun getCreatorString(video: Video, fqdn: Boolean = false): String {
    return if (isChannel(video) && video.channel !== null) {
        if (!fqdn ) {
            video.channel.displayName
        } else {
            getConcatFqdnString(video.channel.name, video.channel.host)
        }
    } else {
        getOwnerString(video.account, fqdn)
    }
}

@Composable
fun getOwnerString(account: Account?, fqdn: Boolean = true): String {
    return  if (!fqdn) {
        account?.name ?: ""
    } else {
        getConcatFqdnString(account?.name, account?.host)
    }
}

@Composable
private fun getConcatFqdnString(user: String? = "", host: String? = ""): String {
    val context = LocalContext.current
    return context.resources.getString(R.string.video_owner_fqdn_line, user, host)
}

@Composable
fun getCreatorAvatar(video: Video): Avatar? {
    return if (isChannel(video)) {
        if (video.channel?.avatar == null) {
            video.account?.avatar
        } else {
            video.channel.avatar
        }
    } else {
        video.account?.avatar
    }
}

@Composable
fun getCreatorAvatarUrl(avatar: Avatar?): String {
    return getImageUrl(avatar?.path)
}

@Composable
fun getImageUrl(image: String?): String {
    return Constants.BASE_IMAGE_URL + image
}

@Composable
fun isChannel(video: Video): Boolean {

    if (video.channel === null) {
        return false
    }

    // c285b523-d688-43c5-a9ad-f745ff09bbd1
    return !video.channel.name.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".toRegex())
}

