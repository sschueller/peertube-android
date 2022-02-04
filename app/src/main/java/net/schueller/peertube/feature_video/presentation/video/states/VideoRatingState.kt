package net.schueller.peertube.feature_video.presentation.video.states

import net.schueller.peertube.feature_video.domain.model.Rating

data class VideoRatingState(
    val isLoading: Boolean = false,
    val rating: Rating? = null,
    val error: String = ""
)
