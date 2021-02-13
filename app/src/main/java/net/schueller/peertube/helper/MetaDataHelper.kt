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
import net.schueller.peertube.R.string
import org.ocpsoft.prettytime.PrettyTime
import java.util.Date
import java.util.Locale

object MetaDataHelper {

    @JvmStatic
    fun getMetaString(getCreatedAt: Date, viewCount: Int, context: Context): String {

        // Compatible with SDK 21+
        val currentLanguage = Locale.getDefault().displayLanguage
        val p = PrettyTime(currentLanguage)
        val relativeTime = p.format(Date(getCreatedAt.time))
        return relativeTime +
                context.resources.getString(string.meta_data_seperator) +
                viewCount + context.resources.getString(string.meta_data_views)
    }

    @JvmStatic
    fun getOwnerString(accountName: String, serverHost: String, context: Context): String {
        return accountName +
                context.resources.getString(string.meta_data_owner_seperator) +
                serverHost
    }

    @JvmStatic
    fun getDuration(duration: Long?): String {
        return DateUtils.formatElapsedTime(duration!!)
    }
}