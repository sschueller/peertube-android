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
package net.schueller.peertube.model

import android.content.Context
import net.schueller.peertube.model.ui.OverviewRecycleViewItem
import net.schueller.peertube.model.Licence
import net.schueller.peertube.model.Privacy
import net.schueller.peertube.model.StreamingPlaylist
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaDescriptionCompat.Builder
import java.util.ArrayList
import java.util.Date

class Video(

    var id: Int,
    var uuid: String,
    var name: String,
    var category: Category,
    var licence: Licence,
    var language: Language,
    var nsfw: Boolean,
    var description: String,
    var local: Boolean,
    var live: Boolean,
    var duration: Int,
    var views: Int,
    var likes: Int,
    var dislikes: Int,
    var thumbnailPath: String,
    var previewPath: String,
    var embedPath: String,
    var createdAt: Date,
    var updatedAt: Date,
    var privacy: Privacy,
    var support: String,
    var descriptionPath: String,
    var channel: Channel,
    var account: Account,
    var tags: ArrayList<String>,
    var commentsEnabled: Boolean,
    var downloadEnabled: Boolean,
    var waitTranscoding: Boolean,
    var state: State,
    var trackerUrls: ArrayList<String>,
    var files: ArrayList<File>,
    var streamingPlaylists: ArrayList<StreamingPlaylist>

): OverviewRecycleViewItem() {

    companion object {

        @JvmStatic
        fun getMediaDescription(context: Context?, video: Video): MediaDescriptionCompat {

//        String apiBaseURL = APIUrlHelper.getUrlWithVersion(context);

//        Bundle extras = new Bundle();
//        Bitmap bitmap = getBitmap(context, Uri.parse(apiBaseURL + video.thumbnailPath));
//        extras.putParcelable(MediaDescriptionCompat.DESCRIPTION_KEY_MEDIA_URI, bitmap);
            return Builder()
                .setMediaId(video.uuid) //                .setIconBitmap(bitmap)
                //                .setExtras(extras)
                .setTitle(video.name)
                .setDescription(video.description)
                .build()
        } //   TODO: add support for the thumbnail
        //    public static Bitmap getBitmap(Context context, Uri fullThumbnailUrl) {
        //
        //         return Picasso.with(context).load(fullThumbnailUrl)
        //                 .placeholder(R.drawable.ic_peertube)
        //                 .error(R.drawable.ic_peertube).get();
        //    }
    }
}