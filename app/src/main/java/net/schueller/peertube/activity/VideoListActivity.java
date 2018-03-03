package net.schueller.peertube.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import net.schueller.peertube.R;
import net.schueller.peertube.adapter.VideoAdapter;
import net.schueller.peertube.model.VideoList;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.services.RecentlyAddedVideosService;
import net.schueller.peertube.model.Video;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.webrtc.ContextUtils.getApplicationContext;

public class VideoListActivity extends AppCompatActivity {

    private VideoAdapter videoAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        // fix android trying to use SSLv3 for handshake
        updateAndroidSecurityProvider(this);

        /*Create handle for the RetrofitInstance interface*/
        GetVideoDataService service = RetrofitInstance.getRetrofitInstance().create(GetVideoDataService.class);

        /*Call the method with parameter in the interface to get the employee data*/
        Call<VideoList> call = service.getVideoData(0, 12, "-createdAt");

        /*Log the URL called*/
        Log.wtf("URL Called", call.request().url() + "");

        call.enqueue(new Callback<VideoList>() {
            @Override
            public void onResponse(Call<VideoList> call, Response<VideoList> response) {
                Log.wtf("Response", response + "");
                generateVideoList(response.body().getVideoArrayList());
            }

            @Override
            public void onFailure(Call<VideoList> call, Throwable t) {
                Log.wtf("err", t.fillInStackTrace());
                Toast.makeText(VideoListActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });


    }


    /*Method to generate List of employees using RecyclerView with custom adapter*/
    private void generateVideoList(ArrayList<Video> vidDataList) {
        recyclerView = findViewById(R.id.recyclerView);

        videoAdapter = new VideoAdapter(vidDataList, VideoListActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoListActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(videoAdapter);
    }


    /**
     * Force android to not use SSLv3
     *
     * @param callingActivity Activity
     */
    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }
}
