package net.schueller.peertube.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import net.schueller.peertube.common.Constants.APP_BACKGROUND_AUDIO_INTENT
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_AUDIO_KEY
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_BEHAVIOR_KEY
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_FLOAT_KEY
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_STOP_KEY
import net.schueller.peertube.common.Constants.PREF_BACK_PAUSE_KEY
import net.schueller.peertube.common.VideoHelper
import net.schueller.peertube.feature_server_address.presentation.address_add_edit.AddEditAddressScreen
import net.schueller.peertube.feature_settings.settings.presentation.SettingsScreen
import net.schueller.peertube.presentation.ui.theme.PeertubeTheme
import net.schueller.peertube.feature_video.presentation.video.VideoListScreen
import net.schueller.peertube.feature_server_address.presentation.ServerAddressScreen
import net.schueller.peertube.feature_server_address.presentation.address_list.AddressListScreen
import net.schueller.peertube.feature_server_address.presentation.server_list.ServerListScreen
import net.schueller.peertube.feature_video.presentation.me.MeScreen
import net.schueller.peertube.feature_video.presentation.video.player.ExoPlayerHolder

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var enteringPIPMode: Boolean = false

    private var receiver: BroadcastReceiver? = null

    val exoPlayerHolder = ExoPlayerHolder

    private val videoHelper = VideoHelper()

    @OptIn(ExperimentalPermissionsApi::class,
        androidx.compose.material.ExperimentalMaterialApi::class,
        androidx.compose.ui.ExperimentalComposeUiApi::class,
        coil.annotation.ExperimentalCoilApi::class
    )
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

                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.VideoListScreen.route
                    ) {
                        composable(
                            route = Screen.VideoListScreen.route
                        ) {
                            VideoListScreen(navController, exoPlayerHolder)
                        }
//                        composable(
//                            route = Screen.VideoPlayScreen.route + "/{uuid}"
//                        ) {
//                            VideoPlayScreen(exoPlayerHolder, navController)
//                        }
//                        composable(
//                            route = Screen.VideoDescriptionScreen.route + "/{uuid}"
//                        ) {
//                            VideoDescriptionScreen()
//                        }
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



    // @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    override fun onBackPressed() {
        Log.v(TAG, "onBackPressed()...")
        val sharedPref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
//        val videoPlayerFragment =
//            (supportFragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?)!!

        // copying Youtube behavior to have back button exit full screen.
        // TODO
//        if (videoPlayerFragment.getIsFullscreen()) {
//            Log.v(TAG, "exiting full screen")
//            videoPlayerFragment.fullScreenToggle()
//            return
//        }
        // pause video if pref is enabled
        if (sharedPref.getBoolean(PREF_BACK_PAUSE_KEY, true)) {
            exoPlayerHolder.pauseVideo()
        }
        val backgroundBehavior = sharedPref.getString(
            PREF_BACKGROUND_BEHAVIOR_KEY,
            PREF_BACKGROUND_STOP_KEY
        )!!
        if (backgroundBehavior == PREF_BACKGROUND_STOP_KEY) {
            Log.v(TAG, "stop the video")
            exoPlayerHolder.pauseVideo()
            stopService(Intent(this, MainActivity::class.java))
            super.onBackPressed()
        } else if (backgroundBehavior == PREF_BACKGROUND_AUDIO_KEY) {
            Log.v(TAG, "play the Audio")
            super.onBackPressed()
        } else if (backgroundBehavior == PREF_BACKGROUND_FLOAT_KEY) {
            Log.v(TAG, "play in floating video")
            //canEnterPIPMode makes sure API level is high enough
            if (videoHelper.canEnterPipMode(this)) {
                Log.v(TAG, "enabling pip")
                enterPIPMode()
                //fixes problem where back press doesn't bring up video list after returning from PIP mode
                val intentSettings = Intent(this, MainActivity::class.java)
                this.startActivity(intentSettings)
            } else {
                Log.v(TAG, "Unable to enter PIP mode")
                super.onBackPressed()
            }
        } else {
            // Deal with bad entries from older version
            Log.v(TAG, "No setting, fallback")
            super.onBackPressed()
        }
    }



    fun enterPIPMode(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (enteringPIPMode) {
                return true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ) {
                enteringPIPMode = true
                val params = PictureInPictureParams.Builder().build()
                try {
                    this.enterPictureInPictureMode(params)
                    return true
                } catch (ex: IllegalStateException) {
                    // pass
                    enteringPIPMode = false
                }
            }
        }
        return false
    }


    //This can only be called when in entering pip mode which can't happen if the device doesn't support pip mode.
    @SuppressLint("NewApi")
    fun makePipControls() {
//        val fragmentManager = supportFragmentManager
//        val videoPlayerFragment =
//            fragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?
        val actions = ArrayList<RemoteAction>()
        var actionIntent = Intent(APP_BACKGROUND_AUDIO_INTENT)
        var pendingIntent =
            PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, actionIntent, 0)
        @SuppressLint("NewApi", "LocalSuppress") var icon = Icon.createWithResource(
            applicationContext, android.R.drawable.stat_sys_speakerphone
        )
        @SuppressLint("NewApi", "LocalSuppress") var remoteAction =
            RemoteAction(icon!!, "close pip", "from pip window custom command", pendingIntent!!)
        actions.add(remoteAction)
        actionIntent = Intent(PlayerNotificationManager.ACTION_STOP)
        pendingIntent =
            PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, actionIntent, 0)
        icon = Icon.createWithResource(
            applicationContext,
            com.google.android.exoplayer2.ui.R.drawable.exo_notification_stop
        )
        remoteAction = RemoteAction(icon, "play", "stop the media", pendingIntent)
        actions.add(remoteAction)
//        assert(videoPlayerFragment != null)
        if (exoPlayerHolder.isPaused) {
            Log.e(TAG, "setting actions with play button")
            actionIntent = Intent(PlayerNotificationManager.ACTION_PLAY)
            pendingIntent =
                PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, actionIntent, 0)
            icon = Icon.createWithResource(
                applicationContext,
                com.google.android.exoplayer2.ui.R.drawable.exo_notification_play
            )
            remoteAction = RemoteAction(icon, "play", "play the media", pendingIntent)
        } else {
            Log.e(TAG, "setting actions with pause button")
            actionIntent = Intent(PlayerNotificationManager.ACTION_PAUSE)
            pendingIntent =
                PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, actionIntent, 0)
            icon = Icon.createWithResource(
                applicationContext,
                com.google.android.exoplayer2.ui.R.drawable.exo_notification_pause
            )
            remoteAction = RemoteAction(icon, "pause", "pause the media", pendingIntent)
        }
        actions.add(remoteAction)


        //add custom actions to pip window
        val params = PictureInPictureParams.Builder()
            .setActions(actions)
            .build()
        setPictureInPictureParams(params)
    }

    fun showControls(value: Boolean) {
        // TODO
//        exoPlayerView!!.useController = value
    }


    private fun changedToPipMode() {
//        val fragmentManager = supportFragmentManager
//        val videoPlayerFragment =
//            (fragmentManager.findFragmentById(R.id.video_player_fragment) as VideoPlayerFragment?)!!
        showControls(false)
        //create custom actions
        makePipControls()

        //setup receiver to handle customer actions
        val filter = IntentFilter()
        filter.addAction(PlayerNotificationManager.ACTION_STOP)
        filter.addAction(PlayerNotificationManager.ACTION_PAUSE)
        filter.addAction(PlayerNotificationManager.ACTION_PLAY)
        filter.addAction(APP_BACKGROUND_AUDIO_INTENT)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action!!
                if (action == PlayerNotificationManager.ACTION_PAUSE) {
                    exoPlayerHolder.pauseVideo()
                    makePipControls()
                }
                if (action == PlayerNotificationManager.ACTION_PLAY) {
                    exoPlayerHolder.unPauseVideo()
                    makePipControls()
                }
                if (action == APP_BACKGROUND_AUDIO_INTENT) {
                    unregisterReceiver(receiver)
                    finish()
                }
                if (action == PlayerNotificationManager.ACTION_STOP) {
                    unregisterReceiver(receiver)
                    finishAndRemoveTask()
                }
            }
        }
        registerReceiver(receiver, filter)
        Log.v(TAG, "switched to pip ")
        floatMode = true
        showControls(false)
    }


    companion object {
        private const val TAG = "MainActivity"
        var floatMode = false
        private const val REQUEST_CODE = 101
    }



}