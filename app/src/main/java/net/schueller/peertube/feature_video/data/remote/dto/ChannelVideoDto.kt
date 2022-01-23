package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.ChannelVideo

data class ChannelVideoDto(
    val channel: ChannelDto,
    val videos: List<VideoDto>
)

fun ChannelVideoDto.toChannelVideo(): ChannelVideo {
    return ChannelVideo(
        channel = channel.toChannel(),
        videos = videos.map { it.toVideo() }
    )
}