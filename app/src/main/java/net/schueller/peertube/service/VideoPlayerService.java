package net.schueller.peertube.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import net.schueller.peertube.R;
import net.schueller.peertube.activity.VideoPlayActivity;
import net.schueller.peertube.helper.MetaDataHelper;
import net.schueller.peertube.model.Video;

import static android.media.session.PlaybackState.ACTION_PAUSE;
import static android.media.session.PlaybackState.ACTION_PLAY;
import static net.schueller.peertube.activity.VideoListActivity.EXTRA_VIDEOID;

public class VideoPlayerService extends Service {

    private static final String TAG = "VideoPlayerService";

    private final IBinder mBinder = new LocalBinder();

    private static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    private static final Integer PLAYBACK_NOTIFICATION_ID = 1;

    public SimpleExoPlayer player;

    private Video currentVideo;
    private String currentStreamUrl;

    private PlayerNotificationManager playerNotificationManager;

    private IntentFilter becomeNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();

    @Override
    public void onCreate() {
        super.onCreate();

        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), new DefaultTrackSelector());

        // Stop player if audio device changes, e.g. headphones unplugged
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playbackState == ACTION_PAUSE) { // this means that pause is available, hence the audio is playing
                    Log.v(TAG, "ACTION_PLAY: " + playbackState);
                    registerReceiver(myNoisyAudioStreamReceiver, becomeNoisyIntentFilter);
                }

                if (playbackState == ACTION_PLAY) { // this means that play is available, hence the audio is paused or stopped
                    Log.v(TAG, "ACTION_PAUSE: " + playbackState);
                    unregisterReceiver(myNoisyAudioStreamReceiver);
                }
            }
        } );

    }

    public class LocalBinder extends Binder {

        public VideoPlayerService getService() {
            // Return this instance of VideoPlayerService so clients can call public methods
            return VideoPlayerService.this;
        }
    }


    @Override
    public void onDestroy() {

        Log.v(TAG, "onDestroy...");

        playerNotificationManager.setPlayer(null);
        player.release();
        player = null;

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand...");
        playVideo();
        return START_STICKY;
    }


    public void setCurrentVideo(Video video)
    {
        Log.v(TAG, "setCurrentVideo...");
        currentVideo = video;
    }

    public void setCurrentStreamUrl(String streamUrl)
    {
        Log.v(TAG, "setCurrentStreamUrl...");
        currentStreamUrl = streamUrl;
    }

    //Playback speed control
    public void setPlayBackSpeed(float speed) {

        Log.v(TAG, "setPlayBackSpeed...");
        player.setPlaybackParameters(new PlaybackParameters(speed));
    }

    public void playVideo() {
        Context context = this;

        Log.v(TAG, "playVideo...");

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                Util.getUserAgent(getApplicationContext(), "PeerTube"), null);

        // This is the MediaSource representing the media to be played.
        ExtractorMediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(currentStreamUrl));

        // Prepare the player with the source.
        player.prepare(videoSource);

        // Auto play
        player.setPlayWhenReady(true);

        //reset playback speed
        this.setPlayBackSpeed(1.0f);

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context, PLAYBACK_CHANNEL_ID, R.string.playback_channel_name,
                PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {

                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return currentVideo.getName();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(context, VideoPlayActivity.class);
                        intent.putExtra(EXTRA_VIDEOID, currentVideo.getUuid());
                        return PendingIntent.getActivity(context, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Override
                    public String getCurrentContentText(Player player) {
                        return MetaDataHelper.getMetaString(
                                currentVideo.getCreatedAt(),
                                currentVideo.getViews(),
                                getBaseContext()
                        );
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        return null;
                    }
                }
        );

        playerNotificationManager.setSmallIcon(R.drawable.ic_peertube_bw);

        // don't show skip buttons in notification
        playerNotificationManager.setUseNavigationActions(false);

        playerNotificationManager.setNotificationListener(
                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationStarted(int notificationId, Notification notification) {
                        startForeground(notificationId, notification);
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId) {
                        Log.v(TAG, "onNotificationCancelled...");

                        // TODO: only kill the notification if we no longer have a bound activity
                        stopForeground(true);
                    }
                }
        );

        playerNotificationManager.setPlayer(player);

    }

    // pause playback on audio output change
    private class BecomingNoisyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                player.setPlayWhenReady(false);
            }
        }
    }

}
