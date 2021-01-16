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
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ServerRepository(application: Application) {

    private val mServerDao: ServerDao

    val allServers: LiveData<List<Server>>
        get() = mServerDao.allServers

    init {
        val db = ServerRoomDatabase.getDatabase(application)
        mServerDao = db.serverDao()
    }

    suspend fun update(server: Server) = withContext(Dispatchers.IO) {
        mServerDao.update(server)
    }

    suspend fun insert(server: Server) = withContext(Dispatchers.IO) {
        mServerDao.insert(server)
    }

    suspend fun delete(server: Server) = withContext(Dispatchers.IO){
        mServerDao.delete(server)
    }
}