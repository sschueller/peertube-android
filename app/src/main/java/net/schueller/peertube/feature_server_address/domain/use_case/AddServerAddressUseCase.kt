package net.schueller.peertube.feature_server_address.domain.use_case

import net.schueller.peertube.feature_server_address.domain.model.InvalidServerAddressException
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import net.schueller.peertube.feature_server_address.domain.repository.ServerAddressRepository

class AddServerAddressUseCase(
    private val repository: ServerAddressRepository
) {

    @Throws(InvalidServerAddressException::class)
    suspend operator fun invoke(serverAddress: ServerAddress) {
        if(serverAddress.serverName.isBlank()) {
            throw InvalidServerAddressException("Server Name is required")
        }

        repository.insertServerAddress(serverAddress)
    }
}