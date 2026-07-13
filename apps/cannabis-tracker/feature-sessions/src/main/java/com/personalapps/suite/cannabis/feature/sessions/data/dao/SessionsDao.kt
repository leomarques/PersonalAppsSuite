package com.personalapps.suite.cannabis.feature.sessions.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.personalapps.suite.cannabis.feature.sessions.data.entities.CannabisLogEntity
import com.personalapps.suite.cannabis.feature.sessions.data.entities.CannabisSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionsDao {
    @Query("SELECT * FROM cannabis_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<CannabisSessionEntity>>

    @Query("SELECT * FROM cannabis_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<CannabisLogEntity>>

    @Query("SELECT * FROM cannabis_logs WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    fun getLogsForSession(sessionId: Long): Flow<List<CannabisLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: CannabisSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CannabisLogEntity): Long

    @Delete
    suspend fun deleteSession(session: CannabisSessionEntity)

    @Delete
    suspend fun deleteLog(log: CannabisLogEntity)
}
