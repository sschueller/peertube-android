package net.schueller.peertube.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import net.schueller.peertube.R;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.helper.MetaDataHelper;
import net.schueller.peertube.model.Video;

import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoPlayActivity extends AppCompatActivity implements VideoRendererEventListener {

    private static final String TAG = "VideoPlayActivity";

    private ProgressBar progressBar;
    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        // get video ID
        Intent intent = getIntent();
        String videoID = intent.getStringExtra(VideoListActivity.EXTRA_VIDEOID);
        Log.v(TAG, "click: " + videoID);

        progressBar = findViewById(R.id.progress);
        progressBar.setMax(100);

//        PlayerView videoView = findViewById(R.id.video_view);
        simpleExoPlayerView = new PlayerView(this);
        simpleExoPlayerView = findViewById(R.id.video_view);

        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        simpleExoPlayerView.setPlayer(player);

        // get video details from api
        String apiBaseURL = APIUrlHelper.getUrl(this);
        GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL + "/api/v1/").create(GetVideoDataService.class);

        Call<Video> call = service.getVideoData(videoID);

        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(@NonNull Call<Video> call, @NonNull Response<Video> response) {

//                Toast.makeText(TorrentVideoPlayActivity.this, response.body().getDescription(), Toast.LENGTH_SHORT).show();

                TextView videoName = findViewById(R.id.name);
                TextView videoDescription = findViewById(R.id.description);
                TextView videoMeta = findViewById(R.id.videoMeta);

                try {

                    videoName.setText(response.body().getName());
                    videoDescription.setText(response.body().getDescription());

                    videoMeta.setText(
                            MetaDataHelper.getMetaString(
                                    response.body().getCreatedAt(),
                                    response.body().getViews(),
                                    getBaseContext()
                            )
                    );

                    String streamUrl = response.body().getFiles().get(0).getFileUrl();

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (sharedPref.getBoolean("pref_torrent_player",false)) {
                        streamUrl = response.body().getFiles().get(0).getTorrentUrl();
                        TorrentStream torrentStream = setupTorrentStream();
                        torrentStream.startStream(streamUrl);
                    } else {
                        setupVideoView(Uri.parse(streamUrl));
                    }

                    Log.v(TAG, streamUrl);


                } catch (NullPointerException e) {
                    e.getStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Video> call, @NonNull Throwable t) {
                Log.wtf(TAG, t.fillInStackTrace());
                Toast.makeText(VideoPlayActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });


    }

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

                setupVideoView(Uri.fromFile(torrent.getVideoFile()));

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

    private void setupVideoView(Uri videoStream) {

        Log.d(TAG, "Play Video");

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                Util.getUserAgent(getApplicationContext(), "PeerTube"), null);

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoStream);

        // Prepare the player with the source.
        player.prepare(videoSource);

        // Auto play
        player.setPlayWhenReady(true);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
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

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

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

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        player.release();
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
        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }
}
