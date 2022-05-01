package net.schueller.peertube.feature_video.presentation.video

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.schueller.peertube.common.Constants
import net.schueller.peertube.common.Resource
import net.schueller.peertube.feature_video.data.remote.auth.Session
import net.schueller.peertube.feature_video.domain.model.Description
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import net.schueller.peertube.feature_video.domain.source.VideoPagingSource
import net.schueller.peertube.feature_video.domain.use_case.*
import net.schueller.peertube.feature_video.presentation.video.events.VideoPlayEvent
import net.schueller.peertube.feature_video.presentation.video.states.VideoDescriptionState
import net.schueller.peertube.feature_video.presentation.video.states.VideoPlayState
import net.schueller.peertube.feature_video.presentation.video.states.VideoRatingState
import javax.inject.Inject

@HiltViewModel
class VideoPlayViewModel @Inject constructor(
    private val videoPlayUseCases: VideoPlayUseCases,
    private val session: Session,
    private val repository: VideoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(VideoPlayState())
    val state: State<VideoPlayState> = _state

    private val _stateVideoDescription = mutableStateOf(VideoDescriptionState())
    val stateVideoDescription: State<VideoDescriptionState> = _stateVideoDescription

    private val _stateVideoRating = mutableStateOf(VideoRatingState())
    val stateVideoRating: State<VideoRatingState> = _stateVideoRating

    var playerVisible = mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

//    init {
//        savedStateHandle.get<String>(Constants.PARAM_VIDEO_UUID)?.let { uuid ->
//            getVideo(uuid)
//            getDescription(uuid)
//        }
//    }

    var relatedVideos: Flow<PagingData<Video>> = Pager(
        PagingConfig(
            pageSize = Constants.VIDEOS_API_PAGE_SIZE,
            maxSize = 100
        )
    ) {
//        if (session.isLoggedIn()) {
//            PlaylistVideoPagingSource(repository, "-publishedAt", video)
//        } else {
            VideoPagingSource(repository, "-publishedAt", null, null ,null)
//        }

    }.flow.cachedIn(viewModelScope)



    private fun getVideo(uuid: String) {
        videoPlayUseCases.getVideoUseCase(uuid).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = VideoPlayState(video = result.data)
                    // Add short description
                    _stateVideoDescription.value = VideoDescriptionState(description = Description(description = result.data?.description ?: "") )
                    if (result.data != null) {
                        getRating(result.data.id)
                    }
                    getDescription(uuid)
                }
                is Resource.Error -> {
                    _state.value = VideoPlayState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _state.value = VideoPlayState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getDescription(uuid: String) {
        // get description data
        videoPlayUseCases.getVideoDescriptionUseCase(uuid).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _stateVideoDescription.value = VideoDescriptionState(description = result.data)

                }
                is Resource.Error -> {
                    _stateVideoDescription.value = VideoDescriptionState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _stateVideoDescription.value = VideoDescriptionState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun onEvent(event: VideoPlayEvent) {
        when (event) {
            is VideoPlayEvent.UpVoteVideo -> {
                if (session.isLoggedIn()) {
                    videoPlayUseCases.upVoteVideoUseCase(event.video).onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                // Update rating
                                if (result.data != null) {
                                    getRating(result.data.id)
                                }
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(
                                    UiEvent.ShowToast(
                                        "Up vote Failed: " + result.message,
                                        Toast.LENGTH_SHORT
                                    )
                                )
                            }
                            is Resource.Loading -> {
                                // Upvote Pending
                            }
                        }
                    }.launchIn(viewModelScope)
                } else {
                    viewModelScope.launch {
                        _eventFlow.emit(
                            UiEvent.ShowToast(
                                "You must be logged in",
                                Toast.LENGTH_SHORT
                            )
                        )
                    }
                }
            }
            is VideoPlayEvent.DownVoteVideo -> {
                if (session.isLoggedIn()) {
                    videoPlayUseCases.downVoteVideoUseCase(event.video).onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                // Update rating
                                if (result.data != null) {
                                    getRating(result.data.id)
                                }
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(
                                    UiEvent.ShowToast(
                                        "Down vote Failed: " + result.message,
                                        Toast.LENGTH_SHORT
                                    )
                                )
                            }
                            is Resource.Loading -> {
                                // Down vote Pending
                            }
                        }
                    }.launchIn(viewModelScope)
                } else {
                    viewModelScope.launch {
                        _eventFlow.emit(
                            UiEvent.ShowToast(
                                "You must be logged in",
                                Toast.LENGTH_SHORT
                            )
                        )
                    }
                }
            }
            is VideoPlayEvent.ShareVideo -> {
                videoPlayUseCases.shareVideoUseCase(event.video)
            }
            is VideoPlayEvent.AddVideoToPlaylist -> {
                videoPlayUseCases.addVideoToPlaylistUseCase(event.video)
            }
            is VideoPlayEvent.BlockVideo -> {
                videoPlayUseCases.blockVideoUseCase(event.video)
            }
            is VideoPlayEvent.FlagVideo -> {
                videoPlayUseCases.flagVideoUseCase(event.video)
            }
            is VideoPlayEvent.DownloadVideo -> {
                // TODO: permissions
                videoPlayUseCases.downloadVideoUseCase(event.video)
            }
            is VideoPlayEvent.OpenDescription -> {
                // Show description before we have the data
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowDescription)
                }
            }
            is VideoPlayEvent.CloseDescription -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.HideDescription)
                }
            }
            is VideoPlayEvent.MoreButton -> {
                // TODO: implement video options menu
                Log.v("VPVM", "Video More Pressed")
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowMore)
                }
            }
            is VideoPlayEvent.MiniPlayerButton -> {
                Log.v("VPVM", "Video Miniplayer Pressed")
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowMiniPlayer)
                }
            }
            is VideoPlayEvent.PlayVideo -> {
                // Load new video
                getVideo(event.video.uuid)
                // show video
                playerVisible.value = true
            }
            is VideoPlayEvent.CloseVideo -> {
                playerVisible.value = false
            }
        }
    }

    private fun getRating(id: Int) {
        Log.v("VPVM", "Get Rating: " + id)
        videoPlayUseCases.getVideoRatingUseCase(id).onEach { res ->
            when (res) {
                is Resource.Success -> {
                    Log.v("VPVM", "Update Rating: " + res.data)
                    _stateVideoRating.value = VideoRatingState(rating = res.data)
                }
                is Resource.Error -> {
                    Log.v("VPVM", "Error getting rating")
                }
                is Resource.Loading -> {
                    // updating rating
                    Log.v("VPVM", "updating rating")
                }
            }
        }.launchIn(viewModelScope)

    }

    sealed class UiEvent {
        data class ShowToast(val message: String, val length: Int): UiEvent()
        object ShowDescription : UiEvent()
        object HideDescription : UiEvent()
        object ShowMore : UiEvent()
        object HideMore : UiEvent()
        object ShowMiniPlayer : UiEvent()
        object HideMiniPlayer : UiEvent()
    }
}