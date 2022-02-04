package net.schueller.peertube.feature_video.presentation.video

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.schueller.peertube.common.Constants.VIDEOS_API_PAGE_SIZE
import net.schueller.peertube.feature_video.data.remote.auth.Session
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import net.schueller.peertube.feature_video.domain.source.VideoPagingSource
import net.schueller.peertube.feature_video.presentation.video.events.*
import net.schueller.peertube.feature_video.presentation.video.states.VideoListState
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val session: Session
) : ViewModel() {

    private val _state = mutableStateOf(VideoListState())
    val state: State<VideoListState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _videos = MutableStateFlow<PagingData<Video>>(PagingData.empty())
    val videos = _videos

    init {
        Log.v("VLM", "INIT")
        getVideos()
    }

    private fun getVideos() {
        viewModelScope.launch {
            Pager(
                PagingConfig(
                    pageSize = VIDEOS_API_PAGE_SIZE,
                    maxSize = 100
                )
            ) {
                VideoPagingSource(
                    repository,
                    _state.value.sort,
                    _state.value.nsfw,
                    _state.value.filter,
                    _state.value.languages
                )
            }.flow.cachedIn(viewModelScope).collect {
                _videos.value = it
            }
        }
    }

    fun onEvent(event: VideoListEvent) {
        when (event) {
            is VideoListEvent.UpdateQuery -> {
                viewModelScope.launch {

                    when (event.set) {
                        SET_DISCOVER -> {
                            _state.value = VideoListState(
                                explore = true,
                                local = false,
                                subscriptions = false,
                                filter = null
                            )
                        }
                        SET_TRENDING -> {
                            _state.value = VideoListState(
                                sort = "-trending",
                                explore = false,
                                local = false,
                                subscriptions = false,
                                filter = null
                            )
                        }
                        SET_RECENT -> {
                            _state.value = VideoListState(
                                sort = "-createdAt",
                                explore = false,
                                local = false,
                                subscriptions = false,
                                filter = null
                            )

                        }
                        SET_LOCAL -> {
                            _state.value = VideoListState(
                                sort = "-publishedAt",
                                explore = false,
                                local = true,
                                subscriptions = false,
                                filter = "local"
                            )
                        }
                        SET_SUBSCRIPTIONS -> {
                            if (session.isLoggedIn()) {
                                _state.value = VideoListState(
                                    explore = false,
                                    local = false,
                                    subscriptions = true,
                                    filter = null
                                )
                            } else {
                                _eventFlow.emit(UiEvent.ShowToast("You must be logged in", Toast.LENGTH_SHORT))
                            }

                        }
                    }

                    getVideos()

                    Log.v("vvm", "Update sort: " + event.set)

                    _eventFlow.emit(UiEvent.ScrollToTop)
                }

            }
        }
    }

    sealed class UiEvent {
        object ScrollToTop : UiEvent()
        data class ShowToast(val message: String, val length: Int): VideoListViewModel.UiEvent()
    }

}