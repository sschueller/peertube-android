package net.schueller.peertube.feature_video.presentation.video_list.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import net.schueller.peertube.feature_video.presentation.video_list.VideoListEvent
import net.schueller.peertube.feature_video.presentation.video_list.VideoListViewModel

@Composable
fun BottomBarComponent(
    navController: NavController
) {
    BottomAppBar(
        content = {
            BottomNavigationBar(navController)
        }
    )
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    videoListViewModel: VideoListViewModel = hiltViewModel()
) {
    val items = listOf(
        BottomBarItems.Discover,
        BottomBarItems.Trending,
        BottomBarItems.Recent,
        BottomBarItems.Local,
        BottomBarItems.Subscriptions
    )
    BottomNavigation(
        contentColor = Color.White
    ) {
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
//                modifier = Modifier.width(105.dp),
                icon = {
                    Icon(painterResource(id = item.icon),
                        contentDescription = item.title
                    )

                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.75f),
                alwaysShowLabel = true,
                selected = false, //currentRoute == item.route,
                onClick = {
                    videoListViewModel.onEvent(
                        VideoListEvent.UpdateQuery(
                            set = item.set
                        )
                    )
                    navController.navigate(item.route) {
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
                        restoreState = false
                    }
                }
            )
        }
    }
}
