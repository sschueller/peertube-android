package net.schueller.peertube.feature_video.presentation.video

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.collectLatest
import net.schueller.peertube.feature_video.domain.model.Overview
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.presentation.video.components.*
import net.schueller.peertube.feature_video.presentation.video.components.appBarBottom.BottomBarComponent
import net.schueller.peertube.feature_video.presentation.video.components.appBarTop.TopAppBarComponent
import net.schueller.peertube.feature_video.presentation.video.components.videoPlay.VideoPlayScreen
import net.schueller.peertube.feature_video.presentation.video.events.VideoPlayEvent
import net.schueller.peertube.feature_video.presentation.video.player.ExoPlayerHolder
import net.schueller.peertube.presentation.Screen
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalCoilApi
@Composable
fun VideoListScreen(
    navController: NavController,
    exoPlayerHolder: ExoPlayerHolder,
    viewModel: VideoListViewModel = hiltViewModel(),
    videoPlayViewModel: VideoPlayViewModel = hiltViewModel(),
    viewExploreModel: VideoExploreViewModel = hiltViewModel()
) {

    val state = viewModel.state.value

    val lazyVideoExploreItems: LazyPagingItems<Overview> = viewExploreModel.videos.collectAsLazyPagingItems()
    val lazyVideoItems: LazyPagingItems<Video> = viewModel.videos.collectAsLazyPagingItems()

    val listState = rememberLazyListState()

    val context = LocalContext.current


    // Events
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is VideoListViewModel.UiEvent.ScrollToTop -> {
                    listState.scrollToItem(index = 0)
                }
                is VideoListViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        event.message,
                        event.length
                    ).show()
                }
            }
        }
    }

    // Auto hide top appbar
    val toolBarHeight = 56.dp
    val toolBarHeightPx = with(LocalDensity.current) { toolBarHeight.roundToPx().toFloat()}
    val toolBarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolBarOffsetHeightPx.value + delta
                toolBarOffsetHeightPx.value = newOffset.coerceIn(-toolBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    )
    {
        // List
        Scaffold(
            bottomBar = {
                BottomBarComponent(navController)
            }
        ) {
            // Pull to refresh
            // TODO: fix appbar blank issue
            SwipeRefresh(
                state = rememberSwipeRefreshState(
                    isRefreshing = (lazyVideoItems.loadState.refresh is LoadState.Loading) || (lazyVideoExploreItems.loadState.refresh is LoadState.Loading)
                ),
                onRefresh = {
                    // Which model do we want to refresh
                    if (state.explore) {
                        lazyVideoExploreItems.refresh()
                    } else {
                        lazyVideoItems.refresh()
                    }
                }
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(top = toolBarHeight),
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (state.explore) {
                        itemsIndexed(lazyVideoExploreItems) { _, overview ->
                            if (overview != null) {
                                // Categories
                                if (overview.categories?.isNotEmpty() == true) {
                                    overview.categories.forEach { categoryVideo ->
                                        VideoCategory(categoryVideo.category)
                                        if (categoryVideo.videos.isNotEmpty()) {
                                            categoryVideo.videos.forEach { video ->
                                                VideoListItem(
                                                    video = video,
                                                    onItemClick = {
                                                        videoPlayViewModel.onEvent(VideoPlayEvent.PlayVideo(video))
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                // Channels
                                if (overview.channels?.isNotEmpty() == true) {
                                    overview.channels.forEach { channelVideo ->
                                        VideoChannel(channelVideo.channel)
                                        if (channelVideo.videos.isNotEmpty()) {
                                            channelVideo.videos.forEach { video ->
                                                VideoListItem(
                                                    video = video,
                                                    onItemClick = {
                                                        videoPlayViewModel.onEvent(VideoPlayEvent.PlayVideo(video))
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                // Tags
                                if (overview.tags?.isNotEmpty() == true) {
                                    overview.tags.forEach { tagVideo ->
                                        VideoTag(tagVideo.tag)
                                        if (tagVideo.videos.isNotEmpty()) {
                                            tagVideo.videos.forEach { video ->
                                                VideoListItem(
                                                    video = video,
                                                    onItemClick = {
                                                        videoPlayViewModel.onEvent(VideoPlayEvent.PlayVideo(video))
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        itemsIndexed(lazyVideoItems) { item, video ->
                            if (video != null) {
//                                Log.v("VLV", video.id.toString() + "-" + item.toString())
                                VideoListItem(
                                    video = video,
                                    onItemClick = {
                                        videoPlayViewModel.onEvent(VideoPlayEvent.PlayVideo(video))
                                    }
                                )
                            }
                        }
                        lazyVideoItems.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item {
    //                                    LoadingView(modifier = Modifier.fillParentMaxSize())
                                    }
                                }
                                loadState.append is LoadState.Loading -> {
                                    item {
    //                                    LoadingItem()
                                    }
                                }
                                loadState.refresh is LoadState.Error -> {
                                    val e = lazyVideoItems.loadState.refresh as LoadState.Error
                                    item {
    //                                    ErrorItem(
    //                                        message = e.error.localizedMessage!!,
    //                                        modifier = Modifier.fillParentMaxSize(),
    //                                        onClickRetry = { retry() }
    //                                    )
                                    }
                                }
                                loadState.append is LoadState.Error -> {
                                    val e = lazyVideoItems.loadState.append as LoadState.Error
                                    item {
    //                                    ErrorItem(
    //                                        message = e.error.localizedMessage!!,
    //                                        onClickRetry = { retry() }
    //                                    )
                                    }
                                }
                            }
                        }
                    }

                    // TODO: deal with errors, https://proandroiddev.com/infinite-lists-with-paging-3-in-jetpack-compose-b095533aefe6

                }

            }

            // Place after list, so it floats above the list in z-height
            TopAppBarComponent(
                navController,
                modifier = Modifier
                    .height(toolBarHeight)
                    .offset {
                        IntOffset(x = 0, y = toolBarOffsetHeightPx.value.roundToInt())
                    }
            )
    //        if(error) {
    //            Text(
    //                text = "Error Text",
    //                color = MaterialTheme.colors.error,
    //                textAlign = TextAlign.Center,
    //                modifier = Modifier
    //                    .fillMaxWidth()
    //                    .padding(horizontal = 20.dp)
    //                    .align(Alignment.Center)
    //            )
    //        }

        }

        if (videoPlayViewModel.playerVisible.value) {
            Log.v("VLS", "Show Video Player")
            VideoPlayScreen(exoPlayerHolder)
        } else {
            Log.v("VLS", "Close Video Player")
        }

    }
}