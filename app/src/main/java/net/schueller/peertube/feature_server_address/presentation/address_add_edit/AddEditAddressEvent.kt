package net.schueller.peertube.feature_server_address.presentation.address_add_edit

import androidx.compose.ui.focus.FocusState
import net.schueller.peertube.feature_server_address.domain.model.Server

sealed class AddEditAddressEvent {
    data class EnteredServerName(val value: String): AddEditAddressEvent()
    data class EnteredServerHost(val value: String): AddEditAddressEvent()
    data class ChangeServerHostFocus(val focusState: FocusState): AddEditAddressEvent()
    data class EnteredUsername(val value: String): AddEditAddressEvent()
    data class EnteredPassword(val value: String): AddEditAddressEvent()
    data class PrefillServerFromSearch(val server: Server): AddEditAddressEvent()

    object SaveServerAddress: AddEditAddressEvent()
}