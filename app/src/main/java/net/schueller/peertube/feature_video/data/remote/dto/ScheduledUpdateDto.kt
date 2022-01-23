package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.ScheduledUpdate
import java.util.*

data class ScheduledUpdateDto(
    val updateAt: Date,
    val privacy: Int,
)

fun ScheduledUpdateDto.toScheduledUpdate(): ScheduledUpdate {
    return ScheduledUpdate(
        updateAt = updateAt,
        privacy = privacy
    )
}