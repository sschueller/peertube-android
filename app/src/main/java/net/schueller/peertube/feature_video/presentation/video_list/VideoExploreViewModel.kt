package net.schueller.peertube.feature_video.presentation.video_list

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
import net.schueller.peertube.feature_video.domain.model.Overview
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import net.schueller.peertube.feature_video.domain.source.ExplorePagingSource
import javax.inject.Inject

@HiltViewModel
class VideoExploreViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel() {

    private val _videos = MutableStateFlow<PagingData<Overview>>(PagingData.empty())
    val videos = _videos

    init {
        getVideos()
    }

    private fun getVideos() {
        viewModelScope.launch {
            Pager(
                PagingConfig(
                    pageSize = 1,
                    maxSize = 5
                )
            ) {
                ExplorePagingSource(repository)
            }.flow.cachedIn(viewModelScope).collect {
                _videos.value = it
            }
        }
    }


}