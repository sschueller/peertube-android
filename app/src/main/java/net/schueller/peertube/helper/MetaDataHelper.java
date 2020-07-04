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

import android.text.format.DateUtils;

import net.schueller.peertube.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;


public class MetaDataHelper {


    public static String getMetaString(Date getCreatedAt, Integer viewCount, Context context) {

        // Compatible with SDK 21+
        String currentLanguage = Locale.getDefault().getDisplayLanguage();
        PrettyTime p = new PrettyTime(currentLanguage);
        String relativeTime = p.format(new Date(getCreatedAt.getTime()));

        return (relativeTime +
                context.getResources().getString(R.string.meta_data_seperator) +
                viewCount + context.getResources().getString(R.string.meta_data_views));
    }

    public static String getOwnerString(String accountName, String serverHost, Context context) {
        return accountName +
                context.getResources().getString(R.string.meta_data_owner_seperator) +
                serverHost;
    }

    public static String getDuration(Long duration) {
        return DateUtils.formatElapsedTime(duration);
    }
}
