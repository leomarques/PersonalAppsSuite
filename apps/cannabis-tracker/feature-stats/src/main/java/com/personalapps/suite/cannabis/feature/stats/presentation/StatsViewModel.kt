package com.personalapps.suite.cannabis.feature.stats.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.cannabis.feature.api.model.CannabisSession
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import kotlinx.coroutines.launch

data class StatsUiState(
    val logs: List<CannabisLog> = emptyList(),
    val sessions: List<CannabisSession> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface StatsEffect {
    data class ShowError(val message: String) : StatsEffect
}

class StatsViewModel(private val repository: SessionsRepository) : BaseViewModel<StatsUiState, StatsEffect>(StatsUiState()) {

    init {
        viewModelScope.launch {
            repository.getAllLogs().collect { logs ->
                updateState { copy(logs = logs, isLoading = false) }
            }
        }
        viewModelScope.launch {
            repository.getAllSessions().collect { sessions ->
                updateState { copy(sessions = sessions) }
            }
        }
    }
}
