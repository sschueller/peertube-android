package net.schueller.peertube.feature_server_address.data.repository

import net.schueller.peertube.feature_server_address.data.data_source.database.dao.ServerAddressDao
import net.schueller.peertube.feature_server_address.domain.repository.ServerAddressRepository
import kotlinx.coroutines.flow.Flow
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress

class ServerAddressRepositoryImpl(
    private val dao: ServerAddressDao
) : ServerAddressRepository {

    override fun getServerAddresses(): Flow<List<ServerAddress>> {
        return dao.getServerAddresses()
    }

    override suspend fun getServerAddressById(id: Int): ServerAddress? {
        return dao.getServerAddressById(id)
    }

    override suspend fun insertServerAddress(serverAddress: ServerAddress) {
        dao.insertServerAddress(serverAddress)
    }

    override suspend fun deleteServerAddress(serverAddress: ServerAddress) {
        dao.deleteServerAddress(serverAddress)
    }
}