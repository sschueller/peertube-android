package net.schueller.peertube.feature_video.presentation.video_list

const val SET_DISCOVER = "discover"
const val SET_LOCAL = "local"
const val SET_RECENT = "recent"
const val SET_TRENDING = "trending"
const val SET_SUBSCRIPTIONS = "subscriptions"

sealed class VideoListEvent {
    data class UpdateQuery(
        val set: String?
    ): VideoListEvent()
}