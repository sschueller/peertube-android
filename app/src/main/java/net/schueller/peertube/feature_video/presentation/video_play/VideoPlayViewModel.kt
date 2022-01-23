package net.schueller.peertube.feature_video.presentation.video_play

import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import net.schueller.peertube.feature_video.domain.source.PlaylistVideoPagingSource
import net.schueller.peertube.feature_video.domain.use_case.*
import javax.inject.Inject

@HiltViewModel
class VideoPlayViewModel @Inject constructor(
    private val getVideoUseCase: GetVideoUseCase,
    private val upVoteVideoUseCase: UpVoteVideoUseCase,
    private val downVoteVideoUseCase: DownVoteVideoUseCase,
    private val shareVideoUseCase: ShareVideoUseCase,
    private val downloadVideoUseCase: DownloadVideoUseCase,
    private val addVideoToPlaylistUseCase: AddVideoToPlaylistUseCase,
    private val blockVideoUseCase: BlockVideoUseCase,
    private val flagVideoUseCase: FlagVideoUseCase,
    private val getVideoRatingUseCase: GetVideoRatingUseCase,
    private val getVideoDescriptionUseCase: GetVideoDescriptionUseCase,
    private val session: Session,
    private val repository: VideoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(VideoPlayState())
    val state: State<VideoPlayState> = _state

    private val _stateVideoDescription = mutableStateOf(VideoDescriptionState())
    val stateVideoDescription: State<VideoDescriptionState> = _stateVideoDescription

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>(Constants.PARAM_VIDEO_UUID)?.let { uuid ->
            getVideo(uuid)
            getDescription(uuid)
        }

    }


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


    private fun getDescription(uuid: String) {
        // get description data
        getVideoDescriptionUseCase(uuid).onEach { result ->
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

    private fun getVideo(uuid: String) {
        getVideoUseCase(uuid).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = VideoPlayState(video = result.data)
                    // Add short description
                    _stateVideoDescription.value = VideoDescriptionState(description = Description(description = result.data?.description ?: "") )
                    if (result.data != null) {
                        getRating(result.data.id)



                    }
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

    fun onEvent(event: VideoPlayEvent) {
        when (event) {
            is VideoPlayEvent.UpVoteVideo -> {
                viewModelScope.launch {
                    if (session.isLoggedIn()) {
                        // TODO: must be logged in
                        upVoteVideoUseCase(event.video).onEach { result ->
                            when (result) {
                                is Resource.Success -> {
                                    // Update rating
                                    if (result.data != null) {
                                        getRating(result.data.id)
                                    }
                                }
                                else -> {
                                }
                            }
                        }.launchIn(viewModelScope)
                    } else {
                        _eventFlow.emit(UiEvent.ShowToast("You must be logged in", Toast.LENGTH_SHORT))
                    }
                }

            }
            is VideoPlayEvent.DownVoteVideo -> {
                viewModelScope.launch {
                    if (session.isLoggedIn()) {
                        // TODO: must be logged in
                        downVoteVideoUseCase(event.video).onEach { result ->
                            when (result) {
                                is Resource.Success -> {
                                    // Update rating
                                    if (result.data != null) {
                                        getRating(result.data.id)
                                    }
                                }
                                else -> {
                                }
                            }
                        }.launchIn(viewModelScope)
                    } else {
                        _eventFlow.emit(UiEvent.ShowToast("You must be logged in", Toast.LENGTH_SHORT))
                    }
                }
            }
            is VideoPlayEvent.ShareVideo -> {
                shareVideoUseCase(event.video)
            }
            is VideoPlayEvent.AddVideoToPlaylist -> {
                addVideoToPlaylistUseCase(event.video)
            }
            is VideoPlayEvent.BlockVideo -> {
                blockVideoUseCase(event.video)
            }
            is VideoPlayEvent.FlagVideo -> {
                flagVideoUseCase(event.video)
            }
            is VideoPlayEvent.DownloadVideo -> {
                // TODO: permissions
                downloadVideoUseCase(event.video)
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
        }
    }

    private fun getRating(id: Int) {
        getVideoRatingUseCase(id).onEach { res ->
            when(res) {
                is Resource.Success -> {
                    _state.value = VideoPlayState(rating = res.data)
                }
                else -> {
                    // error rating
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowToast(val message: String, val length: Int): UiEvent()
        object ShowDescription : UiEvent()
        object HideDescription : UiEvent()
    }
}