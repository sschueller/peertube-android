package net.schueller.peertube.feature_server_address.presentation

sealed class ServerAddressScreen(val route: String) {
    object AddEditAddressScreen: ServerAddressScreen("address_add_edit")
    object AddressListScreen: ServerAddressScreen("address_list")
    object ServerListScreen: ServerAddressScreen("server_list")
}