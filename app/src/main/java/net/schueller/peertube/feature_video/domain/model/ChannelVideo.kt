package net.schueller.peertube.feature_video.domain.model

data class ChannelVideo(
    val channel: Channel,
    val videos: List<Video>
)

