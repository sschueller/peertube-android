package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.TagVideo

data class TagVideoDto(
    var tag: String,
    var videos: List<VideoDto>
)

fun TagVideoDto.toTagVideo(): TagVideo {
    return TagVideo(
        tag = tag,
        videos = videos.map { it.toVideo() }
    )
}