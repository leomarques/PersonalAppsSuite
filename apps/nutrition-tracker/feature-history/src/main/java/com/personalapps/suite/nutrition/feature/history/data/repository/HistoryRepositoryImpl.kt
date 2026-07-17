package com.personalapps.suite.nutrition.feature.history.data.repository

import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import com.personalapps.suite.nutrition.feature.api.repository.HistoryRepository
import com.personalapps.suite.nutrition.feature.history.data.dao.HistoryDao
import com.personalapps.suite.nutrition.feature.history.data.mapper.toDomain
import com.personalapps.suite.nutrition.feature.history.data.mapper.toEntity
import com.personalapps.suite.shared.common.Result
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(private val historyDao: HistoryDao) : HistoryRepository {
    override fun getAllHistory(): Flow<List<HistoryEntry>> {
        return historyDao.getAllHistory().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getHistoryEntryByDate(date: LocalDate): Result<HistoryEntry?> = try {
        Result.Success(historyDao.getHistoryEntryByDate(date)?.toDomain())
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun insertHistoryEntry(entry: HistoryEntry): Result<Unit> = try {
        historyDao.insertHistoryEntry(entry.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun deleteHistoryEntry(entry: HistoryEntry): Result<Unit> = try {
        historyDao.deleteHistoryEntry(entry.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
