package net.schueller.peertube.feature_video.presentation.video_play

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collectLatest
import net.schueller.peertube.feature_server_address.presentation.address_add_edit.AddEditAddressViewModel
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.presentation.video_list.components.VideoListItem
import net.schueller.peertube.feature_video.presentation.video_play.components.VideoDescriptionScreen
import net.schueller.peertube.feature_video.presentation.video_play.components.VideoMeta
import net.schueller.peertube.feature_video.presentation.video_play.components.VideoScreen
import net.schueller.peertube.feature_video.presentation.video_play.player.ExoPlayerHolder
import net.schueller.peertube.presentation.Screen


var enteringPIPMode: Boolean = false

@ExperimentalMaterialApi
@Composable
fun VideoPlayScreen(
    exoPlayerHolder: ExoPlayerHolder,
    navController: NavController,
    viewModel: VideoPlayViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    var descriptionVisible by remember { mutableStateOf(false) }

    // Show toasts
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
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
            }
        }
    }

    // Related Videos
    val lazyRelatedVideoItems: LazyPagingItems<Video> = viewModel.relatedVideos.collectAsLazyPagingItems()


    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {

        val configuration = LocalConfiguration.current


        state.video?.let { video ->

            val systemUiController: SystemUiController = rememberSystemUiController()

            when(configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    // Full screen
                    systemUiController.isStatusBarVisible = false
                    systemUiController.isNavigationBarVisible = false
                    systemUiController.isSystemBarsVisible = false

                    VideoScreen(exoPlayerHolder, video)

                } else -> {
                    systemUiController.isStatusBarVisible = true
                    systemUiController.isNavigationBarVisible = true
                    systemUiController.isSystemBarsVisible = true

                    // TODO: Swipe video down
                    val squareSize = 200.dp
                    val swipeableState = rememberSwipeableState(initialValue = 0)
                    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
                    val anchors = mapOf(sizePx to 1, 0f to 0)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .swipeable(
                                state = swipeableState,
                                anchors = anchors,
                                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                                orientation = Orientation.Vertical,
                            )
                    ) {
                        VideoScreen(exoPlayerHolder, video)

                        Box() {
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
                                                navController.navigate(Screen.VideoPlayScreen.route + "/${video.uuid}")
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
                    }

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

//fun enterPIPMode(activity: Activity): Boolean {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        if (enteringPIPMode) {
//            return true
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
//        ) {
//            enteringPIPMode = true
//            val params = PictureInPictureParams.Builder().build()
//            try {
//                activity.enterPictureInPictureMode(params)
//                return true
//            } catch (ex: IllegalStateException) {
//                // pass
//                enteringPIPMode = false
//            }
//        }
//    }
//    return false
//}


