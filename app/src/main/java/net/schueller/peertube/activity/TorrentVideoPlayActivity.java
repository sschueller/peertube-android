package net.schueller.peertube.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
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

import net.schueller.peertube.R;
import net.schueller.peertube.model.Video;

import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TorrentVideoPlayActivity extends AppCompatActivity {

    private static final String TAG = "TorrentVideoPlayActivity";

    private ProgressBar progressBar;
    private PlayerView videoView;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torrent_video_play);

        // get video ID
        Intent intent = getIntent();
        String videoID = intent.getStringExtra(VideoListActivity.EXTRA_VIDEOID);
        Log.v(TAG, "click: " + videoID);

        progressBar = findViewById(R.id.progress);
        progressBar.setMax(100);

        videoView = findViewById(R.id.video_view);

        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        videoView.setPlayer(player);


        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        TorrentStream torrentStream = TorrentStream.init(torrentOptions);

        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamReady(Torrent torrent) {
                Log.d(TAG, "Ready");

                setupVideoView(torrent);

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

        // get video details from api
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String defaultApiURL = getResources().getString(R.string.api_base_url);
        String apiBaseURL = sharedPref.getString(getString(R.string.api_url_key_key), defaultApiURL);
        GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL + "/api/v1/").create(GetVideoDataService.class);

        Call<Video> call = service.getVideoData(videoID);

        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(@NonNull Call<Video> call, @NonNull Response<Video> response) {

//                Toast.makeText(TorrentVideoPlayActivity.this, response.body().getDescription(), Toast.LENGTH_SHORT).show();

                String streamUrl = null;

                TextView videoName = findViewById(R.id.name);
                TextView videoDescription = findViewById(R.id.description);

                try {
                    streamUrl = response.body().getFiles().get(0).getTorrentUrl();

                    videoName.setText(response.body().getName());
                    videoDescription.setText(response.body().getDescription());

                } catch (NullPointerException e) {
                    e.getStackTrace();
                }

                Log.v(TAG, streamUrl);

                torrentStream.startStream(streamUrl);

            }

            @Override
            public void onFailure(@NonNull Call<Video> call, @NonNull Throwable t) {
                Log.wtf(TAG, t.fillInStackTrace());
                Toast.makeText(TorrentVideoPlayActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setupVideoView(Torrent torrent) {

        Log.d(TAG, "Play Video");

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                Util.getUserAgent(getApplicationContext(), "PeerTube"), null);

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(torrent.getVideoFile()));

        // Auto play
        player.setPlayWhenReady(true);

        // Prepare the player with the source.
        player.prepare(videoSource);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();

    }

    @Override
    protected void onPause() {
        super.onPause();
        player.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
    }
}
