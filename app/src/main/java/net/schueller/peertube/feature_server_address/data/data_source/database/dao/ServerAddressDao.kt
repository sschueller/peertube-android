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
package net.schueller.peertube.feature_server_address.data.data_source.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress

@Dao
interface ServerAddressDao {

    @Query("SELECT * FROM server_table ORDER BY server_name DESC")
    fun getServerAddresses(): Flow<List<ServerAddress>>

    @Query("SELECT * FROM server_table WHERE id = :id")
    suspend fun getServerAddressById(id: Int): ServerAddress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServerAddress(serverAddress: ServerAddress)

    @Delete
    suspend fun deleteServerAddress(serverAddress: ServerAddress)

}