package net.schueller.peertube.presentation.ui.theme

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import net.schueller.peertube.common.Constants.COLOR_PREF_BLUE
import net.schueller.peertube.common.Constants.COLOR_PREF_GREEN
import net.schueller.peertube.common.Constants.COLOR_PREF_RED
import net.schueller.peertube.common.Constants.PREF_DARK_MODE_AUTO
import net.schueller.peertube.common.Constants.PREF_DARK_MODE_DARK

@Composable
fun PeertubeTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    viewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable() () -> Unit
) {

    val useDarkMode = if (viewModel.themeState.value.darkMode == PREF_DARK_MODE_AUTO) {
        useDarkTheme
    } else viewModel.themeState.value.darkMode == PREF_DARK_MODE_DARK

    Log.v("TH", "useDarkMode : "+useDarkMode)
    Log.v("TH", "viewModel : "+viewModel.themeState.value.currentTheme)

    // Support existing saved preferences in older version
    // https://material-foundation.github.io/material-theme-builder/
    val colors = if (!useDarkMode) {
        when (viewModel.themeState.value.currentTheme) {
            COLOR_PREF_BLUE -> {
                Log.v("TH", "use COLOR_PREF_BLUE : "+COLOR_PREF_BLUE)

                net.schueller.peertube.presentation.ui.theme.colors.blue.LightThemeColors
            }
            COLOR_PREF_RED -> {
                Log.v("TH", "use COLOR_PREF_RED : "+COLOR_PREF_RED)

                net.schueller.peertube.presentation.ui.theme.colors.red.LightThemeColors
            }
            COLOR_PREF_GREEN -> {
                Log.v("TH", "use COLOR_PREF_GREEN : "+COLOR_PREF_GREEN)

                net.schueller.peertube.presentation.ui.theme.colors.green.LightThemeColors
            }
            else -> {
                Log.v("TH", "else : ")

                net.schueller.peertube.presentation.ui.theme.colors.def.LightThemeColors
            }
        }
    } else {
        when (viewModel.themeState.value.currentTheme) {
            COLOR_PREF_BLUE -> {
                Log.v("TH", "use COLOR_PREF_BLUE : "+COLOR_PREF_BLUE)
                net.schueller.peertube.presentation.ui.theme.colors.blue.DarkThemeColors
            }
            COLOR_PREF_RED -> {
                Log.v("TH", "use COLOR_PREF_RED : "+COLOR_PREF_RED)
                net.schueller.peertube.presentation.ui.theme.colors.red.DarkThemeColors
            }
            COLOR_PREF_GREEN -> {
                Log.v("TH", "use COLOR_PREF_GREEN : "+COLOR_PREF_GREEN)
                net.schueller.peertube.presentation.ui.theme.colors.green.DarkThemeColors
            }
            else -> {
                Log.v("TH", "else : ")
                net.schueller.peertube.presentation.ui.theme.colors.def.DarkThemeColors
            }
        }
    }

    Log.v("TH", "colors : " + colors.primary)

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}