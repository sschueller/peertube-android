package net.schueller.peertube.feature_server_address.presentation.server_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.schueller.peertube.common.Constants
import net.schueller.peertube.feature_server_address.domain.model.Server
import net.schueller.peertube.feature_server_address.domain.repository.ServerRepository
import net.schueller.peertube.feature_server_address.domain.source.ServerPagingSource
import javax.inject.Inject


@HiltViewModel
class ServerListViewModel @Inject constructor(
    private val repository: ServerRepository
) : ViewModel() {

    private val _state = mutableStateOf(ServerListState())
    val state: State<ServerListState> = _state

    private val _servers = MutableStateFlow<PagingData<Server>>(PagingData.empty())
    val servers = _servers

    init {
        getServers()
    }

    private fun getServers(search: String = "") {
        viewModelScope.launch {
            Pager(
                PagingConfig(
                    pageSize = Constants.SERVERS_API_PAGE_SIZE,
                    maxSize = 100
                )
            ) {
                ServerPagingSource(repository, search)

            }.flow.cachedIn(viewModelScope).collect {
                _servers.value = it
            }
        }
    }

    fun onEvent(event: ServerListEvent) {
        when (event) {
            is ServerListEvent.EnteredSearch -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(
                        searchQuery = event.value
                    )

                    getServers(event.value)
                }
            }

        }
    }
}