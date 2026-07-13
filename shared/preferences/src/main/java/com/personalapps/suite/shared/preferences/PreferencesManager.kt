package com.personalapps.suite.shared.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "personal_apps_suite_prefs")

class PreferencesManager(private val context: Context) {

    fun getThemeMode(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_THEME_MODE] ?: "SYSTEM"
        }
    }

    suspend fun setThemeMode(themeMode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = themeMode
        }
    }

    fun isFeatureEnabled(flagName: String, defaultValue: Boolean = false): Flow<Boolean> {
        val key = booleanPreferencesKey(flagName)
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    suspend fun setFeatureFlag(flagName: String, enabled: Boolean) {
        val key = booleanPreferencesKey(flagName)
        context.dataStore.edit { preferences ->
            preferences[key] = enabled
        }
    }

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("key_theme_mode")
    }
}
