/*
 * Copyright (C) 2020 Stefan Schüller <sschueller@techdroid.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
