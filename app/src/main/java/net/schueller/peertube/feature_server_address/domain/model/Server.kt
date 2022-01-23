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

import java.util.*

data class Server (
    var id: Int? = null,
    var host: String,
    var name: String? = null,
    var shortDescription: String? = null,
    var version: String? = null,
    var signupAllowed: Boolean,
    var userVideoQuota: Double? = null,
    var liveEnabled: Boolean,
    var category: Category? = null,
    var languages: List<String>? = null,
    var autoBlacklistUserVideosEnabled: Boolean,
    var defaultNSFWPolicy: String? = null,
    var isNSFW: Boolean,
    var totalUsers: Int? = null,
    var totalVideos: Int? = null,
    var totalLocalVideos: Int? = null,
    var totalInstanceFollowers: Int? = null,
    var totalInstanceFollowing: Int? = null,
    var supportsIPv6: Boolean,
    var country: String? = null,
    var health: Int? = null,
    var createdAt: Date? = null
)