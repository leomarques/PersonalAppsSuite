package com.personalapps.suite.cannabis.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personalapps.suite.cannabis.data.ProjectionPreferences
import com.personalapps.suite.cannabis.data.ThemePreference
import com.personalapps.suite.cannabis.data.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferences = ThemePreferences(application)
    private val projectionPreferences = ProjectionPreferences(application)

    val theme: StateFlow<ThemePreference> = themePreferences.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemePreference.DYNAMIC
        )

    val projectionIntervalMinutes: StateFlow<Int> = projectionPreferences.intervalMinutesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProjectionPreferences.DEFAULT_INTERVAL_MINUTES
        )

    fun setTheme(theme: ThemePreference) {
        viewModelScope.launch {
            themePreferences.setTheme(theme)
        }
    }

    fun setProjectionIntervalMinutes(minutes: Int) {
        viewModelScope.launch {
            projectionPreferences.setIntervalMinutes(minutes)
        }
    }
}
