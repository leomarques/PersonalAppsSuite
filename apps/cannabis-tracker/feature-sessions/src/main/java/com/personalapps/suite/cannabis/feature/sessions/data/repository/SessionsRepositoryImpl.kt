package com.personalapps.suite.cannabis.feature.sessions.data.repository

import com.personalapps.suite.cannabis.feature.sessions.data.dao.SessionsDao
import com.personalapps.suite.cannabis.feature.sessions.data.entities.CannabisLogEntity
import com.personalapps.suite.cannabis.feature.sessions.data.entities.CannabisSessionEntity
import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.cannabis.feature.api.model.CannabisSession
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun CannabisSessionEntity.toDomain() = CannabisSession(
    id = id,
    title = title,
    startTime = startTime,
    endTime = endTime
)

fun CannabisSession.toEntity() = CannabisSessionEntity(
    id = id,
    title = title,
    startTime = startTime,
    endTime = endTime
)

fun CannabisLogEntity.toDomain() = CannabisLog(
    id = id,
    sessionId = sessionId,
    strainName = strainName,
    method = method,
    amountGrams = amountGrams,
    timestamp = timestamp,
    notes = notes
)

fun CannabisLog.toEntity() = CannabisLogEntity(
    id = id,
    sessionId = sessionId,
    strainName = strainName,
    method = method,
    amountGrams = amountGrams,
    timestamp = timestamp,
    notes = notes
)

class SessionsRepositoryImpl(private val sessionsDao: SessionsDao) : SessionsRepository {
    override fun getAllSessions(): Flow<List<CannabisSession>> =
        sessionsDao.getAllSessions().map { list -> list.map { it.toDomain() } }

    override fun getAllLogs(): Flow<List<CannabisLog>> =
        sessionsDao.getAllLogs().map { list -> list.map { it.toDomain() } }

    override fun getLogsForSession(sessionId: Long): Flow<List<CannabisLog>> =
        sessionsDao.getLogsForSession(sessionId).map { list -> list.map { it.toDomain() } }

    override suspend fun insertSession(session: CannabisSession): Long =
        sessionsDao.insertSession(session.toEntity())

    override suspend fun insertLog(log: CannabisLog): Long =
        sessionsDao.insertLog(log.toEntity())

    override suspend fun deleteSession(session: CannabisSession) {
        sessionsDao.deleteSession(session.toEntity())
    }

    override suspend fun deleteLog(log: CannabisLog) {
        sessionsDao.deleteLog(log.toEntity())
    }
}
