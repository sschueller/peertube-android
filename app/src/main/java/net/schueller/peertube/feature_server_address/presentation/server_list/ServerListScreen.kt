package net.schueller.peertube.feature_server_address.presentation.server_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import net.schueller.peertube.R
import net.schueller.peertube.feature_server_address.domain.model.Server
import net.schueller.peertube.feature_server_address.presentation.address_add_edit.AddEditAddressEvent
import net.schueller.peertube.feature_server_address.presentation.address_add_edit.AddEditAddressViewModel
import net.schueller.peertube.feature_server_address.presentation.server_list.components.ServerListItem

@Composable
fun ServerListScreen (
    navController: NavController,
    viewModel: ServerListViewModel = hiltViewModel(),
    addEditAddressViewModel: AddEditAddressViewModel = hiltViewModel(),
) {
    val lazyServerItems: LazyPagingItems<Server> = viewModel.servers.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    Scaffold(

    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = viewModel.state.value.searchQuery,
                label = {
                    Text(text = "Search")
                },
                onValueChange = {
                    viewModel.onEvent(ServerListEvent.EnteredSearch(it))
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        modifier = Modifier.requiredSize(16.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            )
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        elevation = 12.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material.Icon(
                                    painterResource(id = R.drawable.ic_user_plus),
                                    contentDescription = "signupAllowed",
                                    modifier = Modifier.requiredSize(16.dp),
                                    tint = MaterialTheme.colors.onBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Signup Allowed",
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material.Icon(
                                    painterResource(id = R.drawable.ic_eye_off),
                                    contentDescription = "NSFW",
                                    modifier = Modifier.requiredSize(16.dp),
                                    tint = MaterialTheme.colors.onBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "NSFW Instance",
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material.Icon(
                                    painterResource(id = R.drawable.ic_radio),
                                    contentDescription = "liveEnabled",
                                    modifier = Modifier.requiredSize(16.dp),
                                    tint = MaterialTheme.colors.onBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Live Enabled",
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                        }
                    }
                }
                itemsIndexed(lazyServerItems) { _, server ->
                    if (server != null) {
                        ServerListItem(
                            server = server,
                            onItemClick = {
                                addEditAddressViewModel.onEvent(AddEditAddressEvent.PrefillServerFromSearch(server))
                                navController.navigateUp()
                            }
                        )
                    }
                }
                if (lazyServerItems.loadState.append == LoadState.Loading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
                if (lazyServerItems.loadState.refresh == LoadState.Loading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}
