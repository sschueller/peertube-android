package net.schueller.peertube.feature_video.domain.use_case

import javax.inject.Inject

data class VideoPlayUseCases @Inject constructor(
    val addVideoToPlaylistUseCase: AddVideoToPlaylistUseCase,
    val blockVideoUseCase: BlockVideoUseCase,
    val downloadVideoUseCase: DownloadVideoUseCase,
    val downVoteVideoUseCase: DownVoteVideoUseCase,
    val flagVideoUseCase: FlagVideoUseCase,
    val getVideoDescriptionUseCase: GetVideoDescriptionUseCase,
    val getVideoRatingUseCase: GetVideoRatingUseCase,
    val getVideoUseCase: GetVideoUseCase,
    val shareVideoUseCase: ShareVideoUseCase,
    val upVoteVideoUseCase: UpVoteVideoUseCase,
)