package com.personalapps.suite.nutrition.feature.api.repository

import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import com.personalapps.suite.shared.common.Result
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<HistoryEntry>>
    suspend fun getHistoryEntryByDate(date: LocalDate): Result<HistoryEntry?>
    suspend fun insertHistoryEntry(entry: HistoryEntry): Result<Unit>
    suspend fun deleteHistoryEntry(entry: HistoryEntry): Result<Unit>
}
