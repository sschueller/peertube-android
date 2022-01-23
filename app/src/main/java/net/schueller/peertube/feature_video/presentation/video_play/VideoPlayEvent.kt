package net.schueller.peertube.feature_video.presentation.video_play

import net.schueller.peertube.feature_video.domain.model.Video

sealed class VideoPlayEvent {
    data class ShareVideo(val video: Video): VideoPlayEvent()
    data class UpVoteVideo(val video: Video): VideoPlayEvent()
    data class DownVoteVideo(val video: Video): VideoPlayEvent()
    data class DownloadVideo(val video: Video): VideoPlayEvent()
    data class AddVideoToPlaylist(val video: Video): VideoPlayEvent()
    data class FlagVideo(val video: Video): VideoPlayEvent()
    data class BlockVideo(val video: Video): VideoPlayEvent()
    data class OpenDescription(val video: Video): VideoPlayEvent()
    object CloseDescription: VideoPlayEvent()

}