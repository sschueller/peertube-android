package net.schueller.peertube.feature_server_address.presentation.server_list

sealed class ServerListEvent {
    data class EnteredSearch(val value: String): ServerListEvent()
}