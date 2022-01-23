package net.schueller.peertube.feature_server_address.presentation.address_add_edit

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import net.schueller.peertube.common.UrlHelper
import net.schueller.peertube.feature_server_address.domain.model.InvalidServerAddressException
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress
import net.schueller.peertube.feature_server_address.domain.use_case.ServerAddressUseCases
import javax.inject.Inject

@HiltViewModel
class AddEditAddressViewModel @Inject constructor(
    private val serverAddressUseCases: ServerAddressUseCases,
    private val urlHelper: UrlHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _serverName = mutableStateOf(AddressTextFieldState())
    val serverName: State<AddressTextFieldState> = _serverName

    private val _serverHost = mutableStateOf(AddressTextFieldState())
    val serverHost: State<AddressTextFieldState> = _serverHost

    private val _username = mutableStateOf(AddressTextFieldState())
    val username: State<AddressTextFieldState> = _username

    private val _password = mutableStateOf(AddressTextFieldState())
    val password: State<AddressTextFieldState> = _password

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentServerAddressId: Int = 0

    init {
        savedStateHandle.get<Int>("serverAddressId")?.let { serverAddressId ->
            if(serverAddressId != -1) {
                viewModelScope.launch {
                    serverAddressUseCases.getServerAddress(serverAddressId)?.also { serverAddress ->
                        currentServerAddressId = serverAddress.id
                        _serverName.value = serverName.value.copy(
                            text = serverAddress.serverName
                        )
                        _serverHost.value = serverHost.value.copy(
                            text = serverAddress.serverHost ?: ""
                        )
                        _username.value = username.value.copy(
                            text = serverAddress.username ?: ""
                        )
                        _password.value = password.value.copy(
                            text = serverAddress.password ?: ""
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditAddressEvent) {
        when(event) {
            is AddEditAddressEvent.EnteredServerName -> {
                _serverName.value = _serverName.value.copy(
                    text = event.value
                )
            }

            is AddEditAddressEvent.EnteredServerHost -> {
                _serverHost.value = _serverHost.value.copy(
                    text = event.value
                )
            }

            is AddEditAddressEvent.ChangeServerHostFocus -> {
                if (!event.focusState.isFocused && serverHost.value.text.isNotBlank()) {
                    _serverHost.value = _serverHost.value.copy(
                        text = urlHelper.cleanServerUrl(serverHost.value.text)
                    )
                }

            }
            is AddEditAddressEvent.EnteredUsername -> {
                _username.value = _username.value.copy(
                    text = event.value
                )
            }

            is AddEditAddressEvent.EnteredPassword -> {
                _password.value = _password.value.copy(
                    text = event.value
                )
            }

            is AddEditAddressEvent.PrefillServerFromSearch -> {

                onEvent(AddEditAddressEvent.EnteredServerName(event.server.name ?: ""))
                onEvent(AddEditAddressEvent.EnteredServerHost(event.server.host ?: ""))

                viewModelScope.launch {

                    Log.v("AEAVM", "Selected Server: " + event.server.host.toString())

                    // TODO: doesn't work, textfield is not updated


                    _serverName.value = serverName.value.copy(
                        text = event.server.name ?: ""
                    )
                    _serverHost.value = serverHost.value.copy(
                        text = event.server.host
                    )

                }
            }


            is AddEditAddressEvent.SaveServerAddress -> {
                viewModelScope.launch {
                    try {
                        serverAddressUseCases.addServerAddress(
                            ServerAddress(
                                serverName = serverName.value.text,
                                serverHost = serverHost.value.text,
                                username = username.value.text,
                                password = password.value.text,
                                id = currentServerAddressId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveServerAddress)
                    } catch(e: InvalidServerAddressException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't save Server"
                            )
                        )
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
        object SaveServerAddress: UiEvent()
    }
}