package com.personalapps.suite.cannabis.feature.api.repository

import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.cannabis.feature.api.model.CannabisSession
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
    fun getAllSessions(): Flow<List<CannabisSession>>
    fun getAllLogs(): Flow<List<CannabisLog>>
    fun getLogsForSession(sessionId: Long): Flow<List<CannabisLog>>
    suspend fun insertSession(session: CannabisSession): Long
    suspend fun insertLog(log: CannabisLog): Long
    suspend fun deleteSession(session: CannabisSession)
    suspend fun deleteLog(log: CannabisLog)
}
