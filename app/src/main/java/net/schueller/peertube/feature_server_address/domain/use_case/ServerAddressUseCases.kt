package net.schueller.peertube.feature_server_address.domain.use_case

import net.schueller.peertube.feature_server_address.domain.use_case.AddServerAddress
import net.schueller.peertube.feature_server_address.domain.use_case.DeleteServerAddress
import net.schueller.peertube.feature_server_address.domain.use_case.GetServerAddress
import net.schueller.peertube.feature_server_address.domain.use_case.GetServerAddresses
import net.schueller.peertube.feature_server_address.domain.use_case.SelectServerAddress

data class ServerAddressUseCases(
    val getServerAddresses: GetServerAddresses,
    val deleteServerAddress: DeleteServerAddress,
    val addServerAddress: AddServerAddress,
    val getServerAddress: GetServerAddress,
    val selectServerAddress: SelectServerAddress
)