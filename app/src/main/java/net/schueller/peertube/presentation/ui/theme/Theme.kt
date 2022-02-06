package net.schueller.peertube.presentation.ui.theme

import android.content.Context
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.schueller.peertube.common.Constants
import net.schueller.peertube.common.Constants.COLOR_PREF_BLUE
import net.schueller.peertube.common.Constants.COLOR_PREF_GREEN
import net.schueller.peertube.common.Constants.COLOR_PREF_RED
import net.schueller.peertube.presentation.dataStore


@Composable
fun PeertubeTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)


    // TODO: get pref out of dataStore, migrate old prefs

//    val EXAMPLE_COUNTER = booleanPreferencesKey(Constants.PREF_DARK_MODE_KEY)
//    val useDarkMode: Flow<Boolean> = LocalContext.current.dataStore.data
//        .map { preferences ->
//            preferences[EXAMPLE_COUNTER] ?: useDarkTheme
//    }
//


    val useDarkMode = sharedPreferences.getBoolean(
        Constants.PREF_DARK_MODE_KEY,
        useDarkTheme
    )

    Log.v("TH", "userdark: "+useDarkMode)

    val theme = sharedPreferences.getString(
        Constants.PREF_THEME_KEY,
        COLOR_PREF_BLUE
    )
    Log.v("TH", "theme: "+theme)


    // Support existing saved preferences in older version
    // https://material-foundation.github.io/material-theme-builder/
    val colors = if (!useDarkMode) {
        when (theme) {
            COLOR_PREF_BLUE -> {
                net.schueller.peertube.presentation.ui.theme.colors.blue.LightThemeColors
            }
            COLOR_PREF_RED -> {
                net.schueller.peertube.presentation.ui.theme.colors.red.LightThemeColors
            }
            COLOR_PREF_GREEN -> {
                net.schueller.peertube.presentation.ui.theme.colors.green.LightThemeColors
            }
            else -> {
                net.schueller.peertube.presentation.ui.theme.colors.def.LightThemeColors
            }
        }
    } else {
        when (theme) {
            COLOR_PREF_BLUE -> {
                net.schueller.peertube.presentation.ui.theme.colors.blue.DarkThemeColors
            }
            COLOR_PREF_RED -> {
                net.schueller.peertube.presentation.ui.theme.colors.red.DarkThemeColors
            }
            COLOR_PREF_GREEN -> {
                Log.v("TH", "use green : "+COLOR_PREF_GREEN)

                net.schueller.peertube.presentation.ui.theme.colors.green.DarkThemeColors
            }
            else -> {
                net.schueller.peertube.presentation.ui.theme.colors.def.DarkThemeColors
            }
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}