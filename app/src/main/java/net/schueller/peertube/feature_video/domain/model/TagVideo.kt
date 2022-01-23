package net.schueller.peertube.feature_video.domain.model

data class TagVideo(
    var tag: String,
    var videos: List<Video>
)
