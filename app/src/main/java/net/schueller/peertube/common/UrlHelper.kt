package net.schueller.peertube.common

import android.content.Context
import android.webkit.URLUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import net.schueller.peertube.common.Constants.INVALID_URL_PLACEHOLDER
import net.schueller.peertube.common.Constants.PEERTUBE_API_PATH
import net.schueller.peertube.common.Constants.PREF_API_BASE_KEY
import net.schueller.peertube.common.Constants.VIDEO_SHARE_URI_PATH
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UrlHelper @Inject constructor(
    @ApplicationContext val context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    // Get currently set baseUrl
    private fun getBaseUrl(): String? {

        // validate URL is valid
        val url = sharedPreferences.getString(PREF_API_BASE_KEY, Constants.FALLBACK_BASE_URL)
        return if (!URLUtil.isValidUrl(url)) {
            INVALID_URL_PLACEHOLDER
        } else url
    }

    //
    fun getShareUrl(videoUuid: String): String {
        return getBaseUrl().toString() + VIDEO_SHARE_URI_PATH + videoUuid
    }

    // all servers currently have the same version prefix
    fun getUrlWithVersion(): String {
        return getBaseUrl() + PEERTUBE_API_PATH
    }

    // remove bad characters and add protocol to a server address
    fun cleanServerUrl(url: String?): String {
        if (url != null) {
            var cleanUrl = url.lowercase()
            cleanUrl = cleanUrl.replace(" ", "")
            if (!cleanUrl.startsWith("http")) {
                cleanUrl = "https://$cleanUrl"
            }
            if (cleanUrl.endsWith("/")) {
                cleanUrl = cleanUrl.substring(0, cleanUrl.length - 1)
            }
            return cleanUrl
        }
        return ""
    }



}