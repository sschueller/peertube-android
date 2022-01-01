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
package net.schueller.peertube.helper

import android.content.Context
import android.text.format.DateUtils
import net.schueller.peertube.R
import net.schueller.peertube.R.string
import net.schueller.peertube.model.Account
import net.schueller.peertube.model.Avatar
import net.schueller.peertube.model.Video
import org.ocpsoft.prettytime.PrettyTime
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

object MetaDataHelper {

    @JvmStatic
    fun getMetaString(getCreatedAt: Date, viewCount: Int, context: Context, reversed: Boolean = false): String {

        // Compatible with SDK 21+
        val currentLanguage = Locale.getDefault().displayLanguage
        val p = PrettyTime(currentLanguage)
        val relativeTime = p.format(Date(getCreatedAt.time))
        return if (reversed) {
            viewCount.toString() +
                    context.resources.getString(string.meta_data_views) +
                    context.resources.getString(string.meta_data_seperator) +
                    relativeTime
        } else {
            relativeTime +
                    context.resources.getString(string.meta_data_seperator) +
                    viewCount + context.resources.getString(string.meta_data_views)
        }
    }

    fun getTagsString(video: Video): String {
        return if (video.tags.isNotEmpty()) {
            " #" + video.tags.joinToString(" #", "", "", 3, "")
        } else {
            " "
        }
    }

    @JvmStatic
    fun getCreatorString(video: Video, context: Context, fqdn: Boolean = false): String {
        return if (isChannel(video)) {
            if (!fqdn) {
                video.channel.displayName
            } else {
                getConcatFqdnString(video.channel.name, video.channel.host, context)
            }
        } else {
            getOwnerString(video.account, context, fqdn)
        }
    }

    @JvmStatic
    fun getOwnerString(account: Account, context: Context, fqdn: Boolean = true): String {
        return  if (!fqdn) {
            account.name
        } else {
            getConcatFqdnString(account.name, account.host, context)
        }
    }

    private fun getConcatFqdnString(user: String, host: String, context: Context): String {
        return context.resources.getString(string.video_owner_fqdn_line, user, host)
    }

    @JvmStatic
    fun getCreatorAvatar(video: Video, context: Context): Avatar? {
        return if (isChannel(video)) {
            if (video.channel.avatar == null) {
                video.account.avatar
            } else {
                video.channel.avatar
            }
        } else {
            video.account.avatar
        }
    }

    @JvmStatic
    fun isChannel(video: Video): Boolean {
        // c285b523-d688-43c5-a9ad-f745ff09bbd1
        return !video.channel.name.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".toRegex())
    }



    @JvmStatic
    fun getDuration(duration: Long?): String {
        return DateUtils.formatElapsedTime(duration!!)
    }
}