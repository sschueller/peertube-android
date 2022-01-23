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
package net.schueller.peertube.feature_server_address.domain.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "server_table")
data class ServerAddress(

        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        @ColumnInfo(name = "server_name")
        var serverName: String,

        @ColumnInfo(name = "server_host")
        var serverHost: String? = null,

        @ColumnInfo(name = "username")
        var username: String? = null,

        @ColumnInfo(name = "password")
        var password: String? = null

) : Parcelable

class InvalidServerAddressException(message: String): Exception(message)
