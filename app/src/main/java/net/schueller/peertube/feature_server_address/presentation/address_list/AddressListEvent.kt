package net.schueller.peertube.feature_server_address.presentation.address_list

import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import net.schueller.peertube.feature_server_address.domain.util.ServerAddressOrder


sealed class AddressListEvent {
    data class DeleteServerAddress(val serverAddress: ServerAddress): AddressListEvent()
    data class SelectServerAddress(val serverAddress: ServerAddress): AddressListEvent()
    data class AddEditServerAddress(val serverAddress: ServerAddress): AddressListEvent()
    object RestoreServerAddress: AddressListEvent()
}