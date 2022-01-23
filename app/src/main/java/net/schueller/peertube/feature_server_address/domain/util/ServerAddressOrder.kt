package net.schueller.peertube.feature_server_address.domain.util


sealed class ServerAddressOrder(val orderType: OrderType) {
    class Title(orderType: OrderType): ServerAddressOrder(orderType)
    class Host(orderType: OrderType): ServerAddressOrder(orderType)

    fun copy(orderType: OrderType): ServerAddressOrder {
        return when(this) {
            is Title -> Title(orderType)
            is Host -> Host(orderType)
        }
    }
}