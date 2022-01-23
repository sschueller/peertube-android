package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Redundancy

data class RedundancyDto(
    val baseUrl: String
)

fun RedundancyDto.toRedundancy(): Redundancy {
    return Redundancy(
        baseUrl = baseUrl
    )
}