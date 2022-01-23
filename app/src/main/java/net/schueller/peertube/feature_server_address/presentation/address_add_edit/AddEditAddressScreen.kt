package net.schueller.peertube.feature_server_address.presentation.address_add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import net.schueller.peertube.feature_server_address.presentation.ServerAddressScreen
import net.schueller.peertube.feature_server_address.presentation.address_add_edit.components.AddServerInputTextField

@ExperimentalComposeUiApi
@Composable
fun AddEditAddressScreen(
    navController: NavController,
    viewModel: AddEditAddressViewModel = hiltViewModel()
) {
    val serverNameState = viewModel.serverName.value
    val serverHostState = viewModel.serverHost.value
    val usernameState = viewModel.username.value
    val passwordState = viewModel.password.value

    val scaffoldState = rememberScaffoldState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is AddEditAddressViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is AddEditAddressViewModel.UiEvent.SaveServerAddress -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(AddEditAddressEvent.SaveServerAddress)
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Filled.Done, contentDescription = "Save AddressSave note" )
            }
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate(ServerAddressScreen.ServerListScreen.route)
                }
            ) {
                // Inner content including an icon and a text label
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search for Server",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Search for Server")
            }
            Spacer(modifier = Modifier.height(16.dp))
            AddServerInputTextField(
                text = serverNameState.text,
                hint = "Server Name",
                modifier = Modifier.fillMaxWidth(),
                autofillTypes = emptyList(),
                onFill = {},
                onValueChange = {
                    viewModel.onEvent(AddEditAddressEvent.EnteredServerName(it))
                },
                onFocusChange = {},
                visualTransformation = VisualTransformation.None,
            )
            Spacer(modifier = Modifier.height(16.dp))
            AddServerInputTextField(
                text = serverHostState.text,
                hint = "Server Host",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, autoCorrect = false),
                onValueChange = {
                    viewModel.onEvent(AddEditAddressEvent.EnteredServerHost(it))
                },
                onFocusChange = {
                    viewModel.onEvent(AddEditAddressEvent.ChangeServerHostFocus(it))
                },
                modifier = Modifier.fillMaxWidth(),
                autofillTypes = emptyList(),
                onFill = {},
                visualTransformation = VisualTransformation.None,
            )
            Spacer(modifier = Modifier.height(16.dp))
            AddServerInputTextField(
                text = usernameState.text,
                hint = "Username",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, autoCorrect = false),
                onValueChange = {
                    viewModel.onEvent(AddEditAddressEvent.EnteredUsername(it))
                },
                onFocusChange = {},
                modifier = Modifier
                    .fillMaxWidth(),
                autofillTypes = listOf(AutofillType.Username),
                onFill = {
                    viewModel.onEvent(AddEditAddressEvent.EnteredUsername(it))
                 },
                visualTransformation = VisualTransformation.None,
                )
            Spacer(modifier = Modifier.height(16.dp))
            AddServerInputTextField(
                text = passwordState.text,
                hint = "Password",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = {
                    viewModel.onEvent(AddEditAddressEvent.EnteredPassword(it))
                },
                onFocusChange = {},
                modifier = Modifier.fillMaxWidth(),
                autofillTypes = listOf(AutofillType.Password),
                onFill = {
                    viewModel.onEvent(AddEditAddressEvent.EnteredPassword(it))
                },
                visualTransformation = PasswordVisualTransformation(),
            )
        }
    }
}