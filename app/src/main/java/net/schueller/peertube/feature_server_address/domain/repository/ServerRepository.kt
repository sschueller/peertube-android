package net.schueller.peertube.feature_server_address.domain.repository

import net.schueller.peertube.feature_server_address.domain.model.Server

interface ServerRepository {
    suspend fun getServers(start: Int, count: Int, search: String?): List<Server>
}