package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Description

data class DescriptionDto (
    val description: String
)

fun DescriptionDto.toDescription(): Description {
    return Description(
        description = description,
    )
}