package com.personalapps.suite.cannabis.feature.sessions.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.cannabis.feature.api.model.CannabisSession
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import com.personalapps.suite.cannabis.feature.sessions.domain.usecase.StartSessionUseCase
import com.personalapps.suite.shared.common.Result
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import java.time.Instant
import kotlinx.coroutines.launch

data class SessionsUiState(
    val sessions: List<CannabisSession> = emptyList(),
    val logs: List<CannabisLog> = emptyList(),
    val activeSession: CannabisSession? = null,
    val isLoading: Boolean = true
)

sealed interface SessionsEffect {
    data class ShowError(val message: String) : SessionsEffect
    data object SessionStarted : SessionsEffect
    data object SessionEnded : SessionsEffect
    data object UsageLogged : SessionsEffect
    data object SessionDeleted : SessionsEffect
}

class SessionsViewModel(
    private val repository: SessionsRepository,
    private val startSessionUseCase: StartSessionUseCase
) : BaseViewModel<SessionsUiState, SessionsEffect>(SessionsUiState()) {

    init {
        viewModelScope.launch {
            repository.getAllSessions().collect { sessions ->
                updateState {
                    copy(
                        sessions = sessions,
                        activeSession = sessions.find { it.endTime == null },
                        isLoading = false
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.getAllLogs().collect { logs ->
                updateState { copy(logs = logs) }
            }
        }
    }

    fun startSession(title: String) {
        viewModelScope.launch {
            when (val result = startSessionUseCase(title)) {
                is Result.Success -> sendEffect(SessionsEffect.SessionStarted)
                is Result.Error -> sendEffect(SessionsEffect.ShowError(result.exception.message ?: "Failed to start session"))
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    fun endActiveSession(session: CannabisSession) {
        viewModelScope.launch {
            try {
                repository.insertSession(
                    session.copy(endTime = Instant.now())
                )
                sendEffect(SessionsEffect.SessionEnded)
            } catch (e: Exception) {
                sendEffect(SessionsEffect.ShowError(e.message ?: "Failed to end session"))
            }
        }
    }

    fun logUsage(
        sessionId: Long?,
        strainName: String,
        method: String,
        amountGrams: Float,
        notes: String
    ) {
        if (strainName.isBlank() || method.isBlank() || amountGrams <= 0f) return
        viewModelScope.launch {
            try {
                repository.insertLog(
                    CannabisLog(
                        sessionId = sessionId,
                        strainName = strainName,
                        method = method,
                        amountGrams = amountGrams,
                        timestamp = Instant.now(),
                        notes = notes
                    )
                )
                sendEffect(SessionsEffect.UsageLogged)
            } catch (e: Exception) {
                sendEffect(SessionsEffect.ShowError(e.message ?: "Failed to log usage"))
            }
        }
    }

    fun deleteSession(session: CannabisSession) {
        viewModelScope.launch {
            try {
                repository.deleteSession(session)
                sendEffect(SessionsEffect.SessionDeleted)
            } catch (e: Exception) {
                sendEffect(SessionsEffect.ShowError(e.message ?: "Failed to delete session"))
            }
        }
    }
}
