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
package net.schueller.peertube.intents;

import android.content.Context;
import android.content.Intent;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.Video;


public class Intents {


    /**
     * https://troll.tv/videos/watch/6edbd9d1-e3c5-4a6c-8491-646e2020469c
     *
     * @param context context
     * @param video video
     */
    public static void Share(Context context, Video video) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, video.getName());
        intent.putExtra(Intent.EXTRA_TEXT, APIUrlHelper.getShareUrl(context, video.getUuid()) );
        intent.setType("text/plain");

        context.startActivity(intent);

    }
}
