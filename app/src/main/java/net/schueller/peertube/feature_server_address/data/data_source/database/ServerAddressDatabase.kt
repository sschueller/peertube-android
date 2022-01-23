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
package net.schueller.peertube.feature_server_address.data.data_source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import net.schueller.peertube.feature_server_address.data.data_source.database.dao.ServerAddressDao
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress

@Database(
    entities = [ServerAddress::class],
    version = 1
)
abstract class ServerAddressDatabase: RoomDatabase() {

    abstract val serverAddressDao: ServerAddressDao

    companion object {
        const val DATABASE_NAME = "server_database"
    }
}