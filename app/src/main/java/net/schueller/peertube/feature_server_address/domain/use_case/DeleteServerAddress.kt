package net.schueller.peertube.feature_server_address.domain.use_case

import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import net.schueller.peertube.feature_server_address.domain.repository.ServerAddressRepository

class DeleteServerAddress(
    private val repository: ServerAddressRepository
) {

    suspend operator fun invoke(serverAddress: ServerAddress) {
        repository.deleteServerAddress(serverAddress)
    }
}