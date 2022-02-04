package net.schueller.peertube.feature_video.presentation.video.components.videoPlay

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collectLatest
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.presentation.video.VideoPlayViewModel
import net.schueller.peertube.feature_video.presentation.video.components.VideoListItem
import net.schueller.peertube.feature_video.presentation.video.events.VideoPlayEvent
import net.schueller.peertube.feature_video.presentation.video.player.ExoPlayerHolder


@OptIn(ExperimentalMotionApi::class)
@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
fun VideoPlayScreen(
    exoPlayerHolder: ExoPlayerHolder,
    videoPlayViewModel: VideoPlayViewModel = hiltViewModel()
) {
    val state = videoPlayViewModel.state.value
    val context = LocalContext.current

    var descriptionVisible by remember { mutableStateOf(false) }
    var moreVisible by remember { mutableStateOf(false) }

    // Show toasts
    LaunchedEffect(key1 = true) {
        videoPlayViewModel.eventFlow.collectLatest { event ->
            when(event) {
                is VideoPlayViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        event.message,
                        event.length
                    ).show()
                }
                is VideoPlayViewModel.UiEvent.ShowDescription -> {
                    descriptionVisible = true
                }
                is VideoPlayViewModel.UiEvent.HideDescription -> {
                    descriptionVisible = false
                }
                is VideoPlayViewModel.UiEvent.ShowMore -> {
                    moreVisible = true
                }
                is VideoPlayViewModel.UiEvent.HideMore -> {
                    moreVisible = false
                }
            }
        }
    }

    // Related Videos
    val lazyRelatedVideoItems: LazyPagingItems<Video> = videoPlayViewModel.relatedVideos.collectAsLazyPagingItems()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        val configuration = LocalConfiguration.current

        var animateToEnd by remember { mutableStateOf(false) }
        val progress by animateFloatAsState(
            targetValue = if (animateToEnd) 1f else 0f,
            animationSpec = tween(250)
        )

        state.video?.let { video ->

            val systemUiController: SystemUiController = rememberSystemUiController()

            when(configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    // Full screen
                    // TODO: bottom min bar show on click of screen (Asus Zenfone / Pixel 4)
                    systemUiController.isStatusBarVisible = false
                    systemUiController.isNavigationBarVisible = false
                    systemUiController.isSystemBarsVisible = false

                    VideoScreen(exoPlayerHolder, video, Modifier)

                } else -> {
                    systemUiController.isStatusBarVisible = true
                    systemUiController.isNavigationBarVisible = true
                    systemUiController.isSystemBarsVisible = true

                    Column(
                        modifier = Modifier.background(Color.Transparent).fillMaxSize()
                    ) {
                        MotionLayout(
                            // Large
                           start = ConstraintSet(
                                """ {
                    background: { 
                        width: "spread",
                        height: 400,
                        start: ['parent', 'start', 0],
                        end: ['parent', 'end', 0],
                        top: ['parent', 'top', 0]
                    },
                    v1: { 
                        width: "spread",
                        height: 250,
                        start: ['parent', 'start', 0],
                        end: ['parent', 'end', 0],
                        top: ['parent', 'top', 0]
                    },
                    title: { 
                        width: 0,
                        height: 0,
                        start: ['parent', 'start', 16],
                        top: ['v1', 'bottom', 16],
                        end: ['parent', 'end', 16],
                        custom: {
                          textSize: 20
                        }
                    },
                    description: { 
                        height: 0,
                        width: 0,
                        start: ['parent', 'start', 16],
                        top: ['title', 'bottom', 8],
                        end: ['parent', 'end', 16],
                        custom: {
                          textSize: 16
                        }
                    },
                    list: { 
                        width: "spread",
                        height: 500,
                        start: ['parent', 'start', 0],
                        end: ['parent', 'end', 0],
                        top: ['description', 'bottom', 0]
                    },
                    play: { 
                        start: ['parent', 'end', 8],
                        top: ['v1', 'top', 0],
                        bottom: ['v1', 'bottom', 0]
                    },
                    close: { 
                        start: ['parent', 'end', 8],
                        top: ['v1', 'top', 0],
                        bottom: ['v1', 'bottom', 0]
                    }
                } """
                            ),
                            // small
                           end = ConstraintSet(
                                """ {
                    background: { 
                        width: "spread",
                        height: 60,
                        start: ['parent', 'start', 0],
                        bottom: ['parent', 'bottom', 56],
                        end: ['parent', 'end', 0]
                    },
                    v1: { 
                        width: 100,
                        height: 60,
                        start: ['parent', 'start', 0],
                        bottom: ['parent', 'bottom', 56]
                    },
                    title: { 
                        width: "spread",
                        start: ['v1', 'end', 8],
                        top: ['v1', 'top', 8],
                        end: ['parent', 'end', 8],
                        custom: {
                          textSize: 16
                        }
                    },
                    description: { 
                        start: ['v1', 'end', 8],
                        top: ['title', 'bottom', 0],
                        custom: {
                          textSize: 14
                        }
                    },
                    list: { 
                        width: "spread",
                        height: 0,
                        start: ['parent', 'start', 0],
                        end: ['parent', 'end', 0],
                        top: ['parent', 'bottom', 0]
                    },
                    play: { 
                        end: ['close', 'start', 8],
                        top: ['v1', 'top', 0],
                        bottom: ['v1', 'bottom', 0]
                    },
                    close: { 
                        end: ['parent', 'end', 24],
                        top: ['v1', 'top', 0],
                        bottom: ['v1', 'bottom', 0]
                    }
                } """
                            ),
                            progress = progress,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .layoutId("background", "box")
                                    .background(Color.Blue)
                                    .clickable(onClick = { animateToEnd = !animateToEnd })
                            )

                            VideoScreen(exoPlayerHolder, video,
                                modifier = Modifier
                                    .layoutId("v1", "box")
                                    .background(Color.Black)
                            )

                            Text(
                                text = "MotionLayout in Compose",
                                modifier = Modifier.layoutId("title")
                                    .clickable(onClick = { animateToEnd = !animateToEnd }),
                                color = MaterialTheme.colors.onBackground,
                                fontSize = motionProperties("title").value.fontSize("textSize")
                            )
                            Text(
                                text = "Demo screen 17",
                                modifier = Modifier.layoutId("description"),
                                color = MaterialTheme.colors.onBackground,
                                fontSize = motionProperties("description").value.fontSize("textSize")
                            )

                            Box(
                                modifier = Modifier
                                    .layoutId("list", "box")
                                    .background(MaterialTheme.colors.background)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    item {
                                        VideoMeta(video)
                                    }

                                    itemsIndexed(lazyRelatedVideoItems) { _, video ->
                                        if (video != null) {
                                            VideoListItem(
                                                video = video,
                                                onItemClick = {
                                                    videoPlayViewModel.onEvent(VideoPlayEvent.PlayVideo(video))
                                                }
                                            )
                                        }
                                    }

                                }

                                Column(Modifier.fillMaxSize()) {
                                    AnimatedVisibility(
                                        visible = descriptionVisible,
                                        modifier = Modifier.fillMaxSize(),
                                        enter = slideInVertically(
                                            initialOffsetY = { it }, // it == fullWidth
                                            animationSpec = tween(
                                                durationMillis = 150,
                                                easing = LinearEasing
                                            )
                                        ),
                                        exit = slideOutVertically(
                                            targetOffsetY = { it },
                                            animationSpec = tween(
                                                durationMillis = 150,
                                                easing = LinearEasing
                                            )
                                        )
                                    ) {
                                        VideoDescriptionScreen()
                                    }
                                }
                            }

                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "Play",
                                tint = MaterialTheme.colors.onBackground,
                                modifier = Modifier.layoutId("play")
                            )

                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colors.onBackground,
                                modifier = Modifier.layoutId("close").clickable(
                                    onClick = {
                                        videoPlayViewModel.onEvent(VideoPlayEvent.CloseVideo)
                                    }
                                )
                            )
                        }
                    }
                }
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                AnimatedVisibility(
                    visible = moreVisible,
                    modifier = Modifier.fillMaxSize(),
                    enter = slideInVertically(
                        initialOffsetY = { it }, // it == fullWidth
                        animationSpec = tween(
                            durationMillis = 150,
                            easing = LinearEasing
                        )
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(
                            durationMillis = 150,
                            easing = LinearEasing
                        )
                    )
                ) {
                    VideoMoreScreen()
                }
            }

        }
        if(state.error.isNotBlank()) {
            Text(
                text = state.error,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
        if(state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
//    BackHandler(enabled = true) {
//        Log.v("back", "back pressed")
//
//        enterPIPMode(activity)
//    }
}

