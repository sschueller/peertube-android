package net.schueller.peertube.feature_video.presentation.me

import net.schueller.peertube.feature_video.domain.model.Me

data class MeState(
    val isLoading: Boolean = false,
    val me: Me? = null,
    val error: String = ""
)
