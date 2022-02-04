package net.schueller.peertube.feature_video.presentation.me

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import net.schueller.peertube.feature_video.presentation.me.components.MeAvatar

@ExperimentalCoilApi
@Composable
fun MeScreen (
    navController: NavController,
    viewModel: MeViewModel = hiltViewModel()
) {

//    Column(Modifier.fillMaxSize()) {
//        AnimatedVisibility(
//            visible = true,
//            modifier = Modifier.fillMaxSize(),
//            enter = slideInVertically(
//                initialOffsetY = { it }, // it == fullWidth
//                animationSpec = tween(
//                    durationMillis = 150,
//                    easing = LinearEasing
//                )
//            ),
//            exit = slideOutVertically(
//                targetOffsetY = { it },
//                animationSpec = tween(
//                    durationMillis = 150,
//                    easing = LinearEasing
//                )
//            )
//        ) {

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (viewModel.isLoggedIn) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {

                            }
                        ) {
                            MeAvatar(
                                avatar = viewModel.stateMe.value.me?.account?.avatar,
                                onItemClick = {}
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = viewModel.stateMe.value.me?.username ?: "",
                                fontWeight = FontWeight.Normal,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                viewModel.onEvent(MeEvent.Logout)
                                navController.navigateUp()
                            }
                        ) {
                            Icon(
                                Icons.Filled.ExitToApp,
                                modifier = Modifier
                                    .height(48.dp)
                                    .width(48.dp)
                                    .padding(8.dp),
                                contentDescription = "Logout",
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = "Logout",
                                fontWeight = FontWeight.Normal,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            navController.navigate("settings_screen")
                        }
                    ) {
                        Icon(
                            Icons.Filled.Settings,
                            modifier = Modifier
                                .height(48.dp)
                                .width(48.dp)
                                .padding(8.dp),
                            contentDescription = "Settings",
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = "Settings",
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
//        }
//    }

}
