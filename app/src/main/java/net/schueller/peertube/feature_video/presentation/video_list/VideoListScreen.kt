package net.schueller.peertube.feature_video.presentation.video_list

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import net.schueller.peertube.feature_video.presentation.video_list.components.*
import net.schueller.peertube.presentation.Screen
import kotlin.math.roundToInt

@ExperimentalCoilApi
@Composable
fun VideoListScreen(
    navController: NavController,
    viewModel: VideoListViewModel = hiltViewModel(),
    viewExploreModel: VideoExploreViewModel = hiltViewModel()
) {

    val state = viewModel.state.value

    val lazyVideoExploreItems: LazyPagingItems<Overview> = viewExploreModel.videos.collectAsLazyPagingItems()
    val lazyVideoItems: LazyPagingItems<Video> = viewModel.videos.collectAsLazyPagingItems()

    val listState = rememberLazyListState()

    // Events
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is VideoListViewModel.UiEvent.ScrollToTop -> {
                    listState.scrollToItem(index = 0)
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
        Scaffold(
            bottomBar = {
                BottomBarComponent(navController)
            }
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(
                    isRefreshing = (lazyVideoItems.loadState.refresh is LoadState.Loading) || (lazyVideoExploreItems.loadState.refresh is LoadState.Loading)
                ),
                onRefresh = {
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
                                                        navController.navigate(Screen.VideoPlayScreen.route + "/${video.uuid}")
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
                                                        navController.navigate(Screen.VideoPlayScreen.route + "/${video.uuid}")
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
                                                        navController.navigate(Screen.VideoPlayScreen.route + "/${video.uuid}")
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
                                Log.v("VLV", video.id.toString() + "-" + item.toString())
                                VideoListItem(
                                    video = video,
                                    onItemClick = {
                                        navController.navigate(Screen.VideoPlayScreen.route + "/${video.uuid}")
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
    }
}