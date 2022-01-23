package net.schueller.peertube.feature_video.domain.use_case

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import dagger.hilt.android.qualifiers.ApplicationContext
import net.schueller.peertube.common.UrlHelper
import net.schueller.peertube.feature_video.domain.model.Video
import javax.inject.Inject

class ShareVideoUseCase @Inject constructor(
    var urlHelper: UrlHelper,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(video: Video)
    {
        context.startActivity(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, video.name)
                putExtra(Intent.EXTRA_TEXT, urlHelper.getShareUrl(video.uuid))
                flags = FLAG_ACTIVITY_NEW_TASK // TODO: Is this ok?
            }
        )

    }
}