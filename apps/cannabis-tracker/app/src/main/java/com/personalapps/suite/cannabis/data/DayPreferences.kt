package com.personalapps.suite.cannabis.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dayDataStore: DataStore<Preferences> by preferencesDataStore(name = "day_preferences")

class DayPreferences(private val context: Context) {

    private val activeDayStartKey = longPreferencesKey("active_day_start")
    private val dayStartsHistoryKey = stringPreferencesKey("day_starts_history")

    val activeDayStartFlow: Flow<Long> = context.dayDataStore.data.map { preferences ->
        preferences[activeDayStartKey] ?: 0L
    }

    val dayStartsHistoryFlow: Flow<List<Long>> = context.dayDataStore.data.map { preferences ->
        preferences[dayStartsHistoryKey]
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.map { it.toLong() }
            ?: emptyList()
    }

    suspend fun startNewDay() {
        val now = System.currentTimeMillis()
        context.dayDataStore.edit { preferences ->
            val previousActive = preferences[activeDayStartKey]
            val existingHistory = preferences[dayStartsHistoryKey] ?: ""

            if (previousActive != null && previousActive > 0) {
                val newHistory = if (existingHistory.isBlank()) {
                    previousActive.toString()
                } else {
                    "$existingHistory,$previousActive"
                }
                preferences[dayStartsHistoryKey] = newHistory
            }

            preferences[activeDayStartKey] = now
        }
    }

    suspend fun resetDay() {
        context.dayDataStore.edit { preferences ->
            val previousActive = preferences[activeDayStartKey]
            val existingHistory = preferences[dayStartsHistoryKey] ?: ""

            if (previousActive != null && previousActive > 0) {
                val newHistory = if (existingHistory.isBlank()) {
                    previousActive.toString()
                } else {
                    "$existingHistory,$previousActive"
                }
                preferences[dayStartsHistoryKey] = newHistory
            }

            preferences[activeDayStartKey] = 0L
        }
    }
}
