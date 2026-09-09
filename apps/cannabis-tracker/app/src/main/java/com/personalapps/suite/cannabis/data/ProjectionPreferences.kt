package com.personalapps.suite.cannabis.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.projectionDataStore: DataStore<Preferences> by preferencesDataStore(name = "reminder_preferences")

class ProjectionPreferences(private val context: Context) {

    private val intervalKey = intPreferencesKey("reminder_interval_minutes")

    val intervalMinutesFlow: Flow<Int> = context.projectionDataStore.data.map { preferences ->
        preferences[intervalKey] ?: DEFAULT_INTERVAL_MINUTES
    }

    suspend fun setIntervalMinutes(minutes: Int) {
        context.projectionDataStore.edit { preferences ->
            preferences[intervalKey] = minutes.coerceIn(MIN_MINUTES, MAX_MINUTES)
        }
    }

    companion object {
        const val DEFAULT_INTERVAL_MINUTES = 180
        const val MIN_MINUTES = 30
        const val MAX_MINUTES = 720
    }
}
