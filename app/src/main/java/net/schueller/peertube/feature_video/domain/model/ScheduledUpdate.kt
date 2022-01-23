package net.schueller.peertube.feature_video.domain.model

import java.util.*

data class ScheduledUpdate(
    val updateAt: Date,
    val privacy: Int,
)
