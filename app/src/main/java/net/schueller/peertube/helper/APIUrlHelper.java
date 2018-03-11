package net.schueller.peertube.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.schueller.peertube.R;

public class APIUrlHelper{

    public static String getUrl(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString("pref_api_base", context.getResources().getString(R.string.pref_default_api_base_url));
    }
}
