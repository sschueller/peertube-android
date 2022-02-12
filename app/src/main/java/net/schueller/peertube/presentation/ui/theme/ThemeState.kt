package net.schueller.peertube.presentation.ui.theme

import net.schueller.peertube.common.Constants.COLOR_PREF_DEFAULT
import net.schueller.peertube.common.Constants.PREF_DARK_MODE_AUTO

data class ThemeState(
    val darkMode: String = PREF_DARK_MODE_AUTO,
    val currentTheme: String = COLOR_PREF_DEFAULT
)
