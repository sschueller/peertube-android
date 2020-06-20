/*
 * Copyright 2018 Stefan Schüller <sschueller@techdroid.com>
 *
 * License: GPL-3.0+
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.mikepenz.iconics.Iconics;

import net.schueller.peertube.R;

import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.File;
import net.schueller.peertube.model.Video;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.service.VideoPlayerService;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoPlayerFragment extends Fragment implements VideoRendererEventListener {

    private String mVideoUuid;
    private ProgressBar progressBar;
    private PlayerView simpleExoPlayerView;
    private Intent videoPlayerIntent;
    private Boolean mBound = false;
    private Boolean isFullscreen = false;
    private VideoPlayerService mService;
    private TorrentStream torrentStream;
    private LinearLayout torrentStatus;

    private static final String TAG = "VideoPlayerFragment";


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_player, container, false);
    }


    public void start(String videoUuid) {

        // start service
        Context context = getContext();
        Activity activity = getActivity();

        mVideoUuid = videoUuid;

        assert activity != null;
        progressBar = activity.findViewById(R.id.torrent_progress);
        progressBar.setMax(100);

        simpleExoPlayerView = new PlayerView(context);
        simpleExoPlayerView = activity.findViewById(R.id.video_view);

        simpleExoPlayerView.setControllerShowTimeoutMs(1000);
        simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        torrentStatus = activity.findViewById(R.id.exo_torrent_status);

        // Full screen Icon
        TextView fullscreenText = activity.findViewById(R.id.exo_fullscreen);
        FrameLayout fullscreenButton = activity.findViewById(R.id.exo_fullscreen_button);

        fullscreenText.setText(R.string.video_expand_icon);
        new Iconics.IconicsBuilder().ctx(context).on(fullscreenText).build();

        fullscreenButton.setOnClickListener(view -> {
            Log.d(TAG, "Fullscreen");
            if (!isFullscreen) {
                isFullscreen = true;
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                isFullscreen = false;
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        if (!mBound) {
            videoPlayerIntent = new Intent(context, VideoPlayerService.class);
            activity.bindService(videoPlayerIntent, mConnection, Context.BIND_AUTO_CREATE);
        }


    }

    private void loadVideo() {
        Context context = getContext();


        // get video details from api
        String apiBaseURL = APIUrlHelper.getUrlWithVersion(context);
        GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetVideoDataService.class);

        Call<Video> call = service.getVideoData(mVideoUuid);

        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(@NonNull Call<Video> call, @NonNull Response<Video> response) {

                Video video = response.body();

                mService.setCurrentVideo(video);

                if (video == null) {
                    Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    return;
                }

                playVideo(video);

            }

            @Override
            public void onFailure(@NonNull Call<Video> call, @NonNull Throwable t) {
                Log.wtf(TAG, t.fillInStackTrace());
                Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playVideo(Video video) {

        Context context = getContext();

        // video Meta fragment
        assert getFragmentManager() != null;
        VideoMetaDataFragment videoMetaDataFragment = (VideoMetaDataFragment)
                getFragmentManager().findFragmentById(R.id.video_meta_data_fragment);

        assert videoMetaDataFragment != null;
        videoMetaDataFragment.updateVideoMeta(video, mService);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPref.getBoolean("pref_torrent_player", false)) {
            torrentStatus.setVisibility(View.VISIBLE);
            String stream = video.getFiles().get(0).getTorrentUrl();
            Log.v(TAG, "getTorrentUrl : " + video.getFiles().get(0).getTorrentUrl());
            torrentStream = setupTorrentStream();
            torrentStream.startStream(stream);
        } else {

            Integer videoQuality = sharedPref.getInt("pref_quality", 0);

            //get video qualities
            String urlToPlay = video.getFiles().get(0).getFileUrl();
            for (File file :video.getFiles()) {
                // Set quality if it matches
                if (file.getResolution().getId().equals(videoQuality)) {
                    urlToPlay = file.getFileUrl();
                }
            }
            mService.setCurrentStreamUrl(urlToPlay);

            torrentStatus.setVisibility(View.GONE);
            startPlayer();
        }
        Log.v(TAG, "end of load Video");


    }

    private void startPlayer() {
        Util.startForegroundService(Objects.requireNonNull(getContext()), videoPlayerIntent);
    }


    public void destroyVideo() {
        simpleExoPlayerView.setPlayer(null);
        if (torrentStream != null) {
            torrentStream.stopStream();
        }
    }
    
    public void pauseVideo() {
        mService.player.setPlayWhenReady(false);
    }

    public void stopVideo() {

        if (mBound) {
            Objects.requireNonNull(getContext()).unbindService(mConnection);
            mBound = false;
        }
    }

    public void setIsFullscreen(Boolean fullscreen) {
        isFullscreen = fullscreen;

        TextView fullscreenButton = getActivity().findViewById(R.id.exo_fullscreen);
        if (fullscreen) {
            fullscreenButton.setText(R.string.video_compress_icon);
        } else {
            fullscreenButton.setText(R.string.video_expand_icon);
        }
        new Iconics.IconicsBuilder().ctx(getContext()).on(fullscreenButton).build();
    }

    public Boolean getIsFullscreen() {
        return isFullscreen;
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
                String videopath = Uri.fromFile(torrent.getVideoFile()).toString();
                Log.d(TAG, "Ready! torrentStream videopath:" + videopath);
                mService.setCurrentStreamUrl(videopath);
                startPlayer();
            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {
                if (streamStatus.bufferProgress <= 100 && progressBar.getProgress() < 100 && progressBar.getProgress() != streamStatus.bufferProgress) {
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

}
