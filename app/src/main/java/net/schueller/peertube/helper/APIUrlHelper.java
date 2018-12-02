package net.schueller.peertube.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.URLUtil;

import net.schueller.peertube.R;

public class APIUrlHelper{

    public static String getUrl(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // validate URL is valid
        String URL = sharedPref.getString("pref_api_base", context.getResources().getString(R.string.pref_default_api_base_url));
        if (!URLUtil.isValidUrl(URL)) {
            return "http://invalid";
        }
        return URL;
    }

    public static String getUrlWithVersion(Context context) {
        return APIUrlHelper.getUrl(context) + "/api/v1/";
    }
}
