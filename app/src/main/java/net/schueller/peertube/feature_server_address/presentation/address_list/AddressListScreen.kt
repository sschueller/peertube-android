package net.schueller.peertube.feature_server_address.presentation.address_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.schueller.peertube.R
import net.schueller.peertube.feature_server_address.presentation.ServerAddressScreen
import net.schueller.peertube.feature_server_address.presentation.address_list.components.ServerAddressListItem


//@ExperimentalAnimationApi
@Composable
fun AddressListScreen(
    navController: NavController,
    viewModel: AddressListViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is AddressListViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is AddressListViewModel.UiEvent.SelectServerAddress -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(ServerAddressScreen.AddEditAddressScreen.route)
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_plus),
                    contentDescription = "Add Server"
                )            }
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.serverAddresses) { serverAddress ->

                    ServerAddressListItem(
                        serverAddress = serverAddress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onEvent(AddressListEvent.SelectServerAddress(serverAddress))
                            },
                        onEditClick = {
                            navController.navigate(
                                ServerAddressScreen.AddEditAddressScreen.route +
                            "?serverAddressId=${serverAddress.id}"
                            )
                        },
                        onDeleteClick = {
                            viewModel.onEvent(AddressListEvent.DeleteServerAddress(serverAddress))
                            scope.launch {
                                val result = scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Address deleted",
                                    actionLabel = "Undo"
                                )
                                if(result == SnackbarResult.ActionPerformed) {
                                    viewModel.onEvent(AddressListEvent.RestoreServerAddress)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}