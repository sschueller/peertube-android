package net.schueller.peertube.presentation

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import net.schueller.peertube.feature_server_address.presentation.address_add_edit.AddEditAddressScreen
import net.schueller.peertube.feature_settings.settings.SettingsScreen
import net.schueller.peertube.presentation.ui.theme.PeertubeTheme
import net.schueller.peertube.feature_video.presentation.video_list.VideoListScreen
import net.schueller.peertube.feature_video.presentation.video_play.VideoPlayScreen
import net.schueller.peertube.feature_server_address.presentation.ServerAddressScreen
import net.schueller.peertube.feature_server_address.presentation.address_list.AddressListScreen
import net.schueller.peertube.feature_server_address.presentation.server_list.ServerListScreen
import net.schueller.peertube.feature_video.presentation.me.MeScreen
import net.schueller.peertube.feature_video.presentation.video_play.components.VideoDescriptionScreen
import net.schueller.peertube.feature_video.presentation.video_play.player.ExoPlayerHolder

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val exoPlayerHolder = ExoPlayerHolder

    @ExperimentalCoilApi
    @ExperimentalPermissionsApi
    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeertubeTheme {

                var permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.FOREGROUND_SERVICE,
                    )
                )

//                val lifecycleOwner = LocalLifecycleOwner.current
//                DisposableEffect(
//                    key1 = lifecycleOwner,
//                    effect = {
//                        val observer = LifecycleEventObserver { _, event ->
//                            if (event == Lifecycle.Event.ON_RESUME) {
//                                permissionsState.launchMultiplePermissionRequest()
//                            }
//                        }
//                        lifecycleOwner.lifecycle.addObserver(observer)
//
//                        onDispose {
//                            lifecycleOwner.lifecycle.removeObserver(observer)
//                        }
//                    }
//                )

                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.VideoListScreen.route
                    ) {
                        composable(
                            route = Screen.VideoListScreen.route
                        ) {
                            VideoListScreen(navController)
                        }
                        composable(
                            route = Screen.VideoPlayScreen.route + "/{uuid}"
                        ) {
                            VideoPlayScreen(exoPlayerHolder, navController)
                        }
                        composable(
                            route = Screen.VideoDescriptionScreen.route + "/{uuid}"
                        ) {
                            VideoDescriptionScreen()
                        }
                        composable(
                            route = Screen.SettingsScreen.route
                        ) {
                            SettingsScreen()
                        }
                        // Server Addresses
                        composable(
                            route = ServerAddressScreen.AddressListScreen.route
                        ) {
                            AddressListScreen(navController)
                        }
                        // Me
                        composable(
                            route = Screen.MeScreen.route
                        ) {
                            MeScreen(navController)
                        }
                        composable(
                            route = ServerAddressScreen.AddEditAddressScreen.route +
                                    "?serverAddressId={serverAddressId}",
                            arguments = listOf(
                                navArgument(
                                    name = "serverAddressId"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )

                        ) {
                            AddEditAddressScreen(navController)
                        }
                        composable(
                            route = ServerAddressScreen.ServerListScreen.route
                        ) {
                            ServerListScreen(navController)
                        }
                    }
                }
            }
        }
    }
}