package net.schueller.peertube.service;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import net.schueller.peertube.R;
import net.schueller.peertube.activity.VideoPlayActivity;
import net.schueller.peertube.helper.MetaDataHelper;
import net.schueller.peertube.model.Video;

import java.io.IOException;

import retrofit2.http.FormUrlEncoded;

public class VideoPlayerService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    private static final Integer PLAYBACK_NOTIFICATION_ID = 1;

    public SimpleExoPlayer player;

    private Video currentVideo;
    private String currentStreamUrl;

    private PlayerNotificationManager playerNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v("VideoPlayerService", "onCreate...");

        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), new DefaultTrackSelector());

    }

    public class LocalBinder extends Binder {

        public VideoPlayerService getService() {
            // Return this instance of VideoPlayerService so clients can call public methods
            return VideoPlayerService.this;
        }
    }


    @Override
    public void onDestroy() {

        Log.v("VideoPlayerService", "onDestroy...");

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
        Log.v("VideoPlayerService", "onStartCommand...");
        playVideo();
        return START_STICKY;
    }


    public void setCurrentVideo(Video video)
    {
        Log.v("VideoPlayerService", "setCurrentVideo...");
        currentVideo = video;
    }

    public void setCurrentStreamUrl(String streamUrl)
    {
        Log.v("VideoPlayerService", "setCurrentStreamUrl...");
        currentStreamUrl = streamUrl;
    }

    public void playVideo()
    {
        Context context = this;

        Log.v("VideoPlayerService", "playVideo...");

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
                        return PendingIntent.getActivity(context, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
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

        playerNotificationManager.setNotificationListener(
                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationStarted(int notificationId, Notification notification) {
                        startForeground(notificationId, notification);
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId) {
                        Log.v("VideoPlayerService", "onNotificationCancelled...");

                        // TODO: only kill the notification if we no longer have a bound activity
                        stopForeground(true);
                    }
                }
        );

        playerNotificationManager.setPlayer(player);

    }

}
