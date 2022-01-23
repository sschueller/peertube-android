package net.schueller.peertube.feature_server_address.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import net.schueller.peertube.feature_server_address.domain.repository.ServerAddressRepository
import net.schueller.peertube.feature_server_address.domain.util.OrderType
import net.schueller.peertube.feature_server_address.domain.util.ServerAddressOrder

class GetServerAddresses(
    private val repository: ServerAddressRepository
) {

    operator fun invoke(
        serverAddressOrder: ServerAddressOrder = ServerAddressOrder.Title(OrderType.Descending)
    ): Flow<List<ServerAddress>> {
        return repository.getServerAddresses().map { serverAddresses ->
            when(serverAddressOrder.orderType) {
                is OrderType.Ascending -> {
                    when(serverAddressOrder) {
                        is ServerAddressOrder.Title -> serverAddresses.sortedBy { it.serverName.lowercase() }
                        is ServerAddressOrder.Host -> serverAddresses.sortedBy { it.serverHost?.lowercase() }
                    }
                }
                is OrderType.Descending -> {
                    when(serverAddressOrder) {
                        is ServerAddressOrder.Title -> serverAddresses.sortedByDescending { it.serverName.lowercase() }
                        is ServerAddressOrder.Host -> serverAddresses.sortedByDescending { it.serverHost?.lowercase() }
                    }
                }
            }
        }
    }
}