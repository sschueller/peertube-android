package net.schueller.peertube.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.squareup.picasso.Picasso;
import net.schueller.peertube.R;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.helper.MetaDataHelper;
import net.schueller.peertube.intents.Intents;
import net.schueller.peertube.model.Avatar;
import net.schueller.peertube.model.Video;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.service.VideoPlayerService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.schueller.peertube.helper.Constants.DEFAULT_THEME;
import static net.schueller.peertube.helper.Constants.THEME_PREF_KEY;

public class VideoPlayActivity extends AppCompatActivity implements VideoRendererEventListener {

    private static final String TAG = "VideoPlayActivity";

    private ProgressBar progressBar;
    private PlayerView simpleExoPlayerView;
    private Intent videoPlayerIntent;
    private Context context = this;

    boolean mBound = false;
    VideoPlayerService mService;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            VideoPlayerService.LocalBinder binder = (VideoPlayerService.LocalBinder) service;
            mService = binder.getService();

            // 2. Create the player
            simpleExoPlayerView.setPlayer(mService.player);
            mBound = true;

            loadVideo();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");
            simpleExoPlayerView.setPlayer(null);
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set theme
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(getResources().getIdentifier(
                sharedPref.getString(THEME_PREF_KEY, DEFAULT_THEME),
                "style",
                getPackageName())
        );

        setContentView(R.layout.activity_video_play);

        progressBar = findViewById(R.id.progress);
        progressBar.setMax(100);

        simpleExoPlayerView = new PlayerView(this);
        simpleExoPlayerView = findViewById(R.id.video_view);

        videoPlayerIntent = new Intent(this, VideoPlayerService.class);
        bindService(videoPlayerIntent, mConnection, Context.BIND_AUTO_CREATE);

    }

    private void startPlayer()
    {
        Util.startForegroundService(context, videoPlayerIntent);
    }

    /**
     * Torrent Playback
     *
     * @return torrent stream
     */
    private TorrentStream setupTorrentStream() {

        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        TorrentStream torrentStream = TorrentStream.init(torrentOptions);

        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamReady(Torrent torrent) {
                Log.d(TAG, "Ready");
                mService.setCurrentStreamUrl(Uri.fromFile(torrent.getVideoFile()).toString());
                startPlayer();
            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {
                if(streamStatus.bufferProgress <= 100 && progressBar.getProgress() < 100 && progressBar.getProgress() != streamStatus.bufferProgress) {
                    //Log.d(TAG, "Progress: " + streamStatus.bufferProgress);
                    progressBar.setProgress(streamStatus.bufferProgress);
                }
            }

            @Override
            public void onStreamStopped() {
                Log.d(TAG, "Stopped");
            }

            @Override
            public void onStreamPrepared(Torrent torrent) {
                Log.d(TAG, "Prepared");
            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                Log.d(TAG, "Started");
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d(TAG, "Error: " + e.getMessage());
            }

        });

        return torrentStream;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.v(TAG, "onConfigurationChanged()...");

        super.onConfigurationChanged(newConfig);

        TextView nameView = findViewById(R.id.name);
        TextView videoMetaView = findViewById(R.id.videoMeta);
        TextView descriptionView = findViewById(R.id.description);

        // Checking the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            simpleExoPlayerView.setLayoutParams(params);

            nameView.setVisibility(View.GONE);
            videoMetaView.setVisibility(View.GONE);
            descriptionView.setVisibility(View.GONE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            simpleExoPlayerView.setLayoutParams(params);

            nameView.setVisibility(View.VISIBLE);
            videoMetaView.setVisibility(View.VISIBLE);
            descriptionView.setVisibility(View.VISIBLE);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


    private void loadVideo()
    {
        // get video ID
        Intent intent = getIntent();
        String videoUuid = intent.getStringExtra(VideoListActivity.EXTRA_VIDEOID);
        Log.v(TAG, "click: " + videoUuid);

        // get video details from api
        String apiBaseURL = APIUrlHelper.getUrlWithVersion(this);
        GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetVideoDataService.class);

        Call<Video> call = service.getVideoData(videoUuid);

        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(@NonNull Call<Video> call, @NonNull Response<Video> response) {

//                Toast.makeText(TorrentVideoPlayActivity.this, response.body().getDescription(), Toast.LENGTH_SHORT).show();

                // TODO: remove this code duplication, similar code as in video list rows

                TextView videoName = findViewById(R.id.name);
                TextView videoDescription = findViewById(R.id.description);
                TextView videoOwner = findViewById(R.id.videoOwner);
                TextView videoMeta = findViewById(R.id.videoMeta);
                ImageView avatarView = findViewById(R.id.avatar);
                ImageButton moreButton = findViewById(R.id.moreButton);

                Video video = response.body();

                mService.setCurrentVideo(video);

                String baseUrl = APIUrlHelper.getUrl(context);

                Avatar avatar = video.getAccount().getAvatar();
                if (avatar != null) {
                    String avatarPath = avatar.getPath();
                    Picasso.with(context)
                            .load(baseUrl + avatarPath)
                            .into(avatarView);
                }

                videoName.setText(video.getName());
                videoMeta.setText(
                        MetaDataHelper.getMetaString(
                                video.getCreatedAt(),
                                video.getViews(),
                                getBaseContext()
                        )
                );
                videoOwner.setText(
                        MetaDataHelper.getOwnerString(video.getAccount().getName(),
                                video.getAccount().getHost(),
                                context
                        )
                );
                videoDescription.setText(video.getDescription());

                moreButton.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.setOnMenuItemClickListener(menuItem -> {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_share:
                                Intents.Share(context, video);
                                return true;
                            default:
                                return false;
                        }
                    });
                    popup.inflate(R.menu.menu_video_row_mode);
                    popup.show();
                });

                mService.setCurrentStreamUrl(video.getFiles().get(0).getFileUrl());

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (sharedPref.getBoolean("pref_torrent_player", false)) {

                    String stream = video.getFiles().get(0).getTorrentUrl();
                    TorrentStream torrentStream = setupTorrentStream();
                    torrentStream.startStream(stream);
                } else {
                    startPlayer();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Video> call, @NonNull Throwable t) {
                Log.wtf(TAG, t.fillInStackTrace());
                Toast.makeText(VideoPlayActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {
        Log.v(TAG, "onVideoEnabled()...");

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {
        Log.v(TAG, "onVideoDisabled()...");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayerView.setPlayer(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }

}
