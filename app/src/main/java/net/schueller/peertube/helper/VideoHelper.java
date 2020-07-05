package net.schueller.peertube.helper;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import net.schueller.peertube.R;

public class VideoHelper {

    private static final String TAG = "VideoHelper";

    public static boolean canEnterPipMode(Context context) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        context.getString(R.string.pref_background_float_key);

        // pref is disabled
        if (!context.getString(R.string.pref_background_float_key).equals(
                sharedPref.getString(
                        context.getString(R.string.pref_background_behavior_key),
                        context.getString(R.string.pref_background_float_key))
            )
        ) {
            return false;
        }

        // api does not support it
        Log.v(TAG, "api version " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT > 27) {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            return (AppOpsManager.MODE_ALLOWED == appOpsManager.checkOp(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), context.getPackageName()));
        }
        return false;
    }
}
