package net.schueller.peertube.feature_server_address.presentation.address_list

import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import net.schueller.peertube.feature_server_address.domain.util.OrderType
import net.schueller.peertube.feature_server_address.domain.util.ServerAddressOrder


data class AddressListState(
    val serverAddresses: List<ServerAddress> = emptyList(),
    val serverAddressOrder: ServerAddressOrder = ServerAddressOrder.Title(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false
)