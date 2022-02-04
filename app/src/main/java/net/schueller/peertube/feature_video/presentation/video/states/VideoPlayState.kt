package net.schueller.peertube.feature_video.presentation.video.states

import net.schueller.peertube.feature_video.domain.model.Video

data class VideoPlayState(
    val isLoading: Boolean = false,
    val video: Video? = null,
    val error: String = ""
)
