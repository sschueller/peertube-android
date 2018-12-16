package net.schueller.peertube.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HeadphoneActivity extends BroadcastReceiver {

    private static final String TAG = "HeadphoneActivity";

    private boolean headphonePlug = false;
    private VideoPlayActivity videoPlayActivity;

    public HeadphoneActivity(VideoPlayActivity v)
    {
        this.videoPlayActivity = v;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    if(headphonePlug) {
                        videoPlayActivity.stopPlayer();
                        headphonePlug = false;
                    }
                    //Log.i(TAG, "Headset unplugged");
                    break;
                case 1:
                    headphonePlug = true;
                    //Log.i(TAG, "Headset plugged");
                    break;
                default:
                    break;
            }
        }
    }
}