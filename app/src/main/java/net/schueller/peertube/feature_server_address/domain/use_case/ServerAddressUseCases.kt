package net.schueller.peertube.feature_server_address.domain.use_case

data class ServerAddressUseCases(
    val getServerAddressesUseCase: GetServerAddressesUseCase,
    val deleteServerAddressUseCase: DeleteServerAddressUseCase,
    val addServerAddressUseCase: AddServerAddressUseCase,
    val getServerAddressUseCase: GetServerAddressUseCase,
    val selectServerAddressUseCase: SelectServerAddressUseCase
)