package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Category

data class CategoryDto(
    var id: Int,
    var label: String
)

fun CategoryDto.toCategory(): Category {
    return Category(
        id = id,
        label = label
    )
}