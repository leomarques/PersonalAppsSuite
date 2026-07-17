package com.personalapps.suite.shared.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "personal_apps_suite_prefs")

interface PreferencesManager {
    fun getOpenDayDate(): Flow<String?>
    suspend fun setOpenDayDate(date: String)
}

class PreferencesManagerImpl(private val context: Context) : PreferencesManager {

    override fun getOpenDayDate(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_OPEN_DAY_DATE]
        }
    }

    override suspend fun setOpenDayDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_OPEN_DAY_DATE] = date
        }
    }

    companion object {
        private val KEY_OPEN_DAY_DATE = stringPreferencesKey("key_open_day_date")
    }
}
