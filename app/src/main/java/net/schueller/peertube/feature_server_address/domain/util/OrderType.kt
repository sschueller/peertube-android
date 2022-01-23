package net.schueller.peertube.feature_server_address.domain.util

sealed class OrderType {
    object Ascending: OrderType()
    object Descending: OrderType()
}