package net.schueller.peertube.feature_server_address.data.repository

import net.schueller.peertube.feature_server_address.data.data_source.remote.ServerInstanceApi
import net.schueller.peertube.feature_server_address.data.data_source.remote.dto.toServerList
import net.schueller.peertube.feature_server_address.domain.model.Server
import net.schueller.peertube.feature_server_address.domain.repository.ServerRepository
import javax.inject.Inject

class ServerRepositoryImpl @Inject constructor(
    private val api: ServerInstanceApi
) : ServerRepository {

    override suspend fun getServers(start: Int, count: Int, search: String?): List<Server> {
        return api.getServers(start, count, search).toServerList()
    }

}