package net.schueller.peertube.feature_video.presentation.video_list.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import net.schueller.peertube.R
import net.schueller.peertube.feature_video.presentation.me.MeViewModel
import net.schueller.peertube.feature_video.presentation.me.components.MeAvatar

@ExperimentalCoilApi
@Composable
fun TopAppBarComponent(
    navController: NavController,
    modifier: Modifier,
    meViewModel: MeViewModel = hiltViewModel()
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = "AppBar") },
//        color = Color.White,
        actions = {
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
}
