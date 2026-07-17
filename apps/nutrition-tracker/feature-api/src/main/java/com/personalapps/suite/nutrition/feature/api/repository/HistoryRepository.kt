package com.personalapps.suite.nutrition.feature.api.repository

import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<HistoryEntry>>
    suspend fun getHistoryEntryByDate(date: LocalDate): HistoryEntry?
    suspend fun insertHistoryEntry(entry: HistoryEntry)
    suspend fun deleteHistoryEntry(entry: HistoryEntry)
}
