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
package net.schueller.peertube.database

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class VideoRepository(application: Application) {

    private val mVideoDao: VideoDao

    val allVideos: LiveData<List<Video>>
        get() = mVideoDao.allVideos

    init {
        val db = VideoRoomDatabase.getDatabase(application)
        mVideoDao = db.videoDao()
    }

    suspend fun update(video: Video) = withContext(Dispatchers.IO) {
        mVideoDao.update(video)
    }

    suspend fun insert(video: Video) = withContext(Dispatchers.IO) {
        mVideoDao.insert(video)
    }

    suspend fun delete(video: Video) = withContext(Dispatchers.IO) {
        mVideoDao.delete(video)
    }
}