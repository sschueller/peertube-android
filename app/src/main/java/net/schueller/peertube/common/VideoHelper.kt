package net.schueller.peertube.common

import net.schueller.peertube.feature_video.domain.model.Video
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoHelper @Inject constructor() {

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
}

