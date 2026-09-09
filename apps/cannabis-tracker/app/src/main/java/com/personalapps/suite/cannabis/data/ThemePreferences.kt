package com.personalapps.suite.cannabis.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme_preference")

    val themeFlow: Flow<ThemePreference> = context.dataStore.data.map { preferences ->
        when (preferences[themeKey]) {
            "light" -> ThemePreference.LIGHT
            "dark" -> ThemePreference.DARK
            "dynamic" -> ThemePreference.DYNAMIC
            else -> ThemePreference.DYNAMIC
        }
    }

    suspend fun setTheme(theme: ThemePreference) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = when (theme) {
                ThemePreference.LIGHT -> "light"
                ThemePreference.DARK -> "dark"
                ThemePreference.DYNAMIC -> "dynamic"
            }
        }
    }
}
