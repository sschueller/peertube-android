package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.CategoryVideo

data class CategoryVideoDto (
    val category: CategoryDto,
    val videos: List<VideoDto>
)

fun CategoryVideoDto.toCategoryVideo(): CategoryVideo {
    return CategoryVideo(
        category = category.toCategory(),
        videos = videos.map { it.toVideo() }
    )
}