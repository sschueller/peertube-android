package net.schueller.peertube.feature_settings.settings.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.schueller.peertube.feature_settings.settings.domain.repository.SettingsRepository
import net.schueller.peertube.presentation.dataStore

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override suspend fun getBooleanSettings(key: String, default: Boolean): Flow<Boolean> {
        val dataStoreKey = booleanPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[dataStoreKey] ?: default
        }
    }

    override suspend fun getStringSettings(key: String, default: String): Flow<String> {
        val dataStoreKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[dataStoreKey] ?: default
        }
    }

    override suspend fun getIntegerSettings(key: String, default: Int): Flow<Int> {
        val dataStoreKey = intPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[dataStoreKey] ?: default
        }
    }

}