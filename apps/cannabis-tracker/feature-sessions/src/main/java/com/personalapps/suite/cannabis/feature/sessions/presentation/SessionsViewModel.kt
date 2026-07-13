package com.personalapps.suite.cannabis.feature.sessions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.cannabis.feature.api.model.CannabisSession
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import com.personalapps.suite.cannabis.feature.sessions.domain.usecase.StartSessionUseCase
import java.time.Instant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionsViewModel(
    private val repository: SessionsRepository,
    private val startSessionUseCase: StartSessionUseCase
) : ViewModel() {

    val sessionsState: StateFlow<List<CannabisSession>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logsState: StateFlow<List<CannabisLog>> = repository.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSessionState: StateFlow<CannabisSession?> = sessionsState
        .map { list -> list.find { it.endTime == null } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun startSession(title: String) {
        viewModelScope.launch {
            startSessionUseCase(title)
        }
    }

    fun endActiveSession(session: CannabisSession) {
        viewModelScope.launch {
            repository.insertSession(
                session.copy(endTime = Instant.now())
            )
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
        }
    }

    fun deleteSession(session: CannabisSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
}
