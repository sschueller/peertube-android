package net.schueller.peertube.common

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton
import android.webkit.URLUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import net.schueller.peertube.common.Constants.PREF_API_BASE_KEY


@Singleton
class UrlHelper @Inject constructor(
    @ApplicationContext val context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    fun getUrl(): String? {

        // validate URL is valid
        val url = sharedPreferences.getString(PREF_API_BASE_KEY, "TODO")
        return if (!URLUtil.isValidUrl(url)) {
            "http://invalid"
        } else url
    }

    fun getShareUrl(videoUuid: String): String {
        return getUrl().toString() + "/videos/watch/" + videoUuid
    }


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