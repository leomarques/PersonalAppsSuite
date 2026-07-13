package com.personalapps.suite.cannabis.feature.history.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import kotlinx.coroutines.launch

data class HistoryUiState(
    val logs: List<CannabisLog> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface HistoryEffect {
    data class ShowError(val message: String) : HistoryEffect
    data object LogDeleted : HistoryEffect
}

class HistoryViewModel(private val repository: SessionsRepository) : BaseViewModel<HistoryUiState, HistoryEffect>(HistoryUiState()) {

    init {
        viewModelScope.launch {
            repository.getAllLogs().collect { logs ->
                updateState { copy(logs = logs, isLoading = false) }
            }
        }
    }

    fun deleteLog(log: CannabisLog) {
        viewModelScope.launch {
            try {
                repository.deleteLog(log)
                sendEffect(HistoryEffect.LogDeleted)
            } catch (e: Exception) {
                sendEffect(HistoryEffect.ShowError(e.message ?: "Failed to delete log"))
            }
        }
    }
}
