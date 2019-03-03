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
package net.schueller.peertube.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.webkit.URLUtil;
import android.widget.Toast;

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

    public static String getShareUrl(Context context, String videoUuid) {
        return APIUrlHelper.getUrl(context) + "/videos/watch/" + videoUuid;
    }

    public static String getServerIndexUrl(Context context) {
        return "https://instances.joinpeertube.org/api/v1/";
    }

    public static String cleanServerUrl(String url) {

        String cleanUrl = url.toLowerCase();

        cleanUrl = cleanUrl.replace(" ", "");

        if (!cleanUrl.startsWith("http")) {
            cleanUrl = "https://" + cleanUrl;
        }

        if (cleanUrl.endsWith("/")) {
            cleanUrl = cleanUrl.substring(0, cleanUrl.length() - 1);
        }

        return cleanUrl;
    }
}
