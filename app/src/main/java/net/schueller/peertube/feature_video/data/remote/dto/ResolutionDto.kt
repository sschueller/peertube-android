package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Resolution

data class ResolutionDto(
    val id: Int,
    val label: String
)

fun ResolutionDto.toResolution(): Resolution {
    return Resolution(
        id = id,
        label = label
    )
}