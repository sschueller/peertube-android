package net.schueller.peertube.feature_video.domain.model

data class CategoryVideo (
    val category: Category,
    val videos: List<Video>
)