package net.schueller.peertube.feature_video.domain.model

data class Overview(
    val categories: List<CategoryVideo>?,
    val channels: List<ChannelVideo>?,
    val tags: List<TagVideo>?
)
