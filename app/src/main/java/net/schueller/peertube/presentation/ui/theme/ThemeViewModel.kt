package net.schueller.peertube.presentation.ui.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.schueller.peertube.common.Constants.COLOR_PREF_DEFAULT
import net.schueller.peertube.common.Constants.PREF_DARK_MODE_AUTO
import net.schueller.peertube.common.Constants.PREF_DARK_MODE_KEY
import net.schueller.peertube.common.Constants.PREF_THEME_KEY
import net.schueller.peertube.feature_settings.settings.domain.repository.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(private  val settingsRepository: SettingsRepository): ViewModel() {

    private val _themeState = mutableStateOf(ThemeState())
    val themeState: State<ThemeState> = _themeState

    init {
        viewModelScope.launch {
            settingsRepository
                .getStringSettings(PREF_THEME_KEY, COLOR_PREF_DEFAULT).collect {
                    _themeState.value = _themeState.value.copy(
                        currentTheme = it
                    )
                }
            settingsRepository
                .getStringSettings(PREF_DARK_MODE_KEY, PREF_DARK_MODE_AUTO).collect {
                    _themeState.value = _themeState.value.copy(
                        darkMode = it
                    )
                }
        }
    }
}