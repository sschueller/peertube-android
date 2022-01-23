package net.schueller.peertube.feature_video.presentation.me

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.schueller.peertube.common.Resource
import net.schueller.peertube.feature_video.domain.use_case.GetMeUseCase
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(
    private val getMeUseCase: GetMeUseCase
) : ViewModel() {

    private val _stateMe = mutableStateOf(MeState())
    val stateMe: State<MeState> = _stateMe

    init {
        getMe()
    }

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
}