package net.schueller.peertube.feature_video.presentation.video_list

data class VideoListState(
    val sort: String? = null,
    val filter: String? = null,
    val nsfw: String? = null,
    val languages: Set<String?>? = null,
    val explore: Boolean = false,
    val local: Boolean = false,
    val subscriptions: Boolean = false
)
