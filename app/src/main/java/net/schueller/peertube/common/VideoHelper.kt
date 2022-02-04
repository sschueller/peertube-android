package net.schueller.peertube.common

import net.schueller.peertube.feature_video.domain.model.Video
import javax.inject.Inject
import javax.inject.Singleton
import android.app.AppOpsManager

import android.os.Build

import android.R
import android.content.Context

import android.preference.PreferenceManager

import android.content.SharedPreferences
import android.os.Process
import dagger.hilt.android.qualifiers.ApplicationContext
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_BEHAVIOR_KEY
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_FLOAT_KEY


@Singleton
class VideoHelper @Inject constructor(
) {



    fun pickPlaybackResolution(video: Video, preferredQuality: Int = 999999): String?
    {
        var urlToPlay: String? = null

        if (video.streamingPlaylists != null && video.streamingPlaylists.isNotEmpty()) {
            urlToPlay = video.streamingPlaylists[0].playlistUrl
        } else {
            if (video.files != null && video.files.isNotEmpty()) {
                urlToPlay = video.files[0].fileUrl // default, take first found, usually highest res
                for (file in video.files) {
                    // Set quality if it matches
                    if (file.resolution.id == preferredQuality) {
                        urlToPlay = file.fileUrl
                    }
                }
            }
        }

        return urlToPlay
    }


    fun canEnterPipMode(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

        // pref is disabled
        if (!PREF_BACKGROUND_FLOAT_KEY.equals(
                sharedPreferences.getString(
                    PREF_BACKGROUND_BEHAVIOR_KEY,
                    PREF_BACKGROUND_FLOAT_KEY
                )
            )
        ) {
            return false
        }

        // api does not support it
        if (Build.VERSION.SDK_INT > 27) {
            val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            return AppOpsManager.MODE_ALLOWED == appOpsManager.checkOp(
                AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                Process.myUid(),
                context.packageName
            )
        }
        return false
    }

}

