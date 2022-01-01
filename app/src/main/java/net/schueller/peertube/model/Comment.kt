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

import java.util.*


class Comment(

    val id: Int,
    val url: String,
    val text: String,
    val threadId: Int,
    val inReplyToCommentId: Int? = null,
    val videoId: Int,
    val createdAt: Date,
    val updatedAt: Date,
    val deletedAt: Date? = null,
    val isDeleted: Boolean,
    val totalRepliesFromVideoAuthor: Int,
    val totalReplies: Int,
    val account: Account

)