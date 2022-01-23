package net.schueller.peertube.feature_server_address.presentation.address_list


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.schueller.peertube.feature_server_address.domain.model.InvalidServerAddressException
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import net.schueller.peertube.feature_server_address.domain.use_case.ServerAddressUseCases
import net.schueller.peertube.feature_server_address.domain.util.OrderType
import net.schueller.peertube.feature_server_address.domain.util.ServerAddressOrder
import net.schueller.peertube.feature_server_address.presentation.address_add_edit.AddEditAddressViewModel
import javax.inject.Inject

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val serverAddressUseCases: ServerAddressUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AddressListState())
    val state: State<AddressListState> = _state

    private var recentlyDeletedServerAddress: ServerAddress? = null

    private var getServerAddressesJob: Job? = null

    private val _eventFlow = MutableSharedFlow<AddressListViewModel.UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getServerAddresses(ServerAddressOrder.Title(OrderType.Descending))
    }

    fun onEvent(event: AddressListEvent) {
        when (event) {
            is AddressListEvent.DeleteServerAddress -> {
                viewModelScope.launch {
                    serverAddressUseCases.deleteServerAddress(event.serverAddress)
                    recentlyDeletedServerAddress = event.serverAddress
                }
            }
            is AddressListEvent.SelectServerAddress -> {
                viewModelScope.launch {
                    try {
                        serverAddressUseCases.selectServerAddress(
                            event.serverAddress
                        )
                        _eventFlow.emit(UiEvent.SelectServerAddress)
                    } catch(e: InvalidServerAddressException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't Select Server Address"
                            )
                        )
                    }
                }
            }
            is AddressListEvent.RestoreServerAddress -> {
                viewModelScope.launch {
                    serverAddressUseCases.addServerAddress(recentlyDeletedServerAddress ?: return@launch)
                    recentlyDeletedServerAddress = null
                }
            }
            is AddressListEvent.AddEditServerAddress -> {

            }
        }
    }

    private fun getServerAddresses(serverAddressOrder: ServerAddressOrder) {
        getServerAddressesJob?.cancel()
        getServerAddressesJob = serverAddressUseCases.getServerAddresses(serverAddressOrder)
            .onEach { serverAddresses ->
                _state.value = state.value.copy(
                    serverAddresses = serverAddresses,
                    serverAddressOrder = serverAddressOrder
                )
            }
            .launchIn(viewModelScope)
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
        object SelectServerAddress: UiEvent()
    }
}