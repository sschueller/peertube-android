package net.schueller.peertube.feature_video.presentation.me

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.schueller.peertube.common.Resource
import net.schueller.peertube.feature_video.data.remote.auth.Session
import net.schueller.peertube.feature_video.domain.use_case.GetMeUseCase
import net.schueller.peertube.feature_video.domain.use_case.LogoutUseCase
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(
    private val getMeUseCase: GetMeUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val session: Session
) : ViewModel() {

    private val _stateMe = mutableStateOf(MeState())
    val stateMe: State<MeState> = _stateMe

    init {
        getMe()
    }

    val isLoggedIn = session.isLoggedIn()

    private fun getMe() {
        // get description data
        getMeUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _stateMe.value = MeState(me = result.data)

                }
                is Resource.Error -> {
                    _stateMe.value = MeState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _stateMe.value = MeState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun onEvent(event: MeEvent) {
        when (event) {
            MeEvent.Logout -> {
                logoutUseCase()
            }
        }
    }

}