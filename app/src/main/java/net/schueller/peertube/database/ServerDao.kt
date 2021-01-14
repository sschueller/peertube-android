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

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ServerDao {

    @Insert
    suspend fun insert(server: Server)

    @Update
    suspend fun update(server: Server)

    @Query("DELETE FROM server_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(server: Server)

    @get:Query("SELECT * from server_table ORDER BY server_name DESC")
    val allServers: LiveData<List<Server>>
}