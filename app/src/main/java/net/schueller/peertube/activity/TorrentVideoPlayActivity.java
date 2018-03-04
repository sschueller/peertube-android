package net.schueller.peertube.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;

import net.schueller.peertube.R;
import net.schueller.peertube.model.Video;
import net.schueller.peertube.model.VideoList;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TorrentVideoPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torrent_video_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // get video ID
        Intent intent = getIntent();
        String videoID = intent.getStringExtra(VideoListActivity.EXTRA_VIDEOID);
        Log.v("TorrentVideoPlayActivity", "click: " + videoID);


        // get video details from api
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String defaultApiURL = getResources().getString(R.string.api_base_url);
        String apiBaseURL = sharedPref.getString(getString(R.string.api_url_key_key), defaultApiURL);
        GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL + "/api/v1/").create(GetVideoDataService.class);

        Call<Video> call = service.getVideoData(videoID);

        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(@NonNull Call<Video> call, @NonNull Response<Video> response) {

                Toast.makeText(TorrentVideoPlayActivity.this, response.body().getDescription(), Toast.LENGTH_SHORT).show();

                // response.body().getFiles()
//                TorrentOptions torrentOptions = new TorrentOptions.Builder()
//                        .removeFilesAfterStop(true)
//                        .build();
//
//                TorrentStream torrentStream = TorrentStream.init(torrentOptions);
//                torrentStream.startStream("https://butterpoject.org/test.torrent");

            }

            @Override
            public void onFailure(@NonNull Call<Video> call, @NonNull Throwable t) {
                Log.wtf("err", t.fillInStackTrace());
                Toast.makeText(TorrentVideoPlayActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });



    }

}
