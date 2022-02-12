package net.schueller.peertube.feature_settings.settings.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun getBooleanSettings(key: String, default: Boolean): Flow<Boolean>
    suspend fun getStringSettings(key: String, default: String): Flow<String>
    suspend fun getIntegerSettings(key: String, default: Int): Flow<Int>

}