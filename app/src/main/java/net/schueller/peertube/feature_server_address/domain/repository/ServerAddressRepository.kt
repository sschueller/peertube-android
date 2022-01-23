package net.schueller.peertube.feature_server_address.domain.repository

import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import kotlinx.coroutines.flow.Flow

interface ServerAddressRepository {

    fun getServerAddresses(): Flow<List<ServerAddress>>

    suspend fun getServerAddressById(id: Int): ServerAddress?

    suspend fun insertServerAddress(serverAddress: ServerAddress)

    suspend fun deleteServerAddress(serverAddress: ServerAddress)
}