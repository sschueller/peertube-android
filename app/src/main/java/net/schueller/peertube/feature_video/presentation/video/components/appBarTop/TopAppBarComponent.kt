package net.schueller.peertube.feature_video.presentation.video.components.appBarTop

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import net.schueller.peertube.R
import net.schueller.peertube.feature_video.presentation.me.MeViewModel
import net.schueller.peertube.feature_video.presentation.me.components.MeAvatar
import net.schueller.peertube.feature_video.presentation.video.components.VideoSearch

@OptIn(ExperimentalAnimationApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@ExperimentalCoilApi
@Composable
fun TopAppBarComponent(
    navController: NavController,
    modifier: Modifier,
    meViewModel: MeViewModel = hiltViewModel()
) {
    var searchBarVisible by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = { Text(text = "AppBar") },
//        color = Color.White,
        actions = {
            IconButton(
                modifier = Modifier,
                onClick = {
                    searchBarVisible = true
                }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = {
                navController.navigate("address_list") {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }) {
                Icon(
                    painterResource(id = R.drawable.ic_server),
                    contentDescription = "Address Book"
                )
            }
            MeAvatar(
                avatar = meViewModel.stateMe.value.me?.account?.avatar,
                onItemClick = {
                    navController.navigate("me_screen")
                }
            )
        }
    )

    if (searchBarVisible) {
        VideoSearch(
            hide = {
                searchBarVisible = false
            }
        )
    }

}
