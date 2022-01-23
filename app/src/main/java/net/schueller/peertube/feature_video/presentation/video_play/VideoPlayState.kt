package net.schueller.peertube.feature_video.presentation.video_play

import net.schueller.peertube.feature_video.domain.model.Description
import net.schueller.peertube.feature_video.domain.model.Rating
import net.schueller.peertube.feature_video.domain.model.Video

data class VideoPlayState(
    val isLoading: Boolean = false,
    val video: Video? = null,
    val rating: Rating? = null,
    val error: String = ""
)
