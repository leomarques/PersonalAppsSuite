package com.personalapps.suite.nutrition.feature.history.data.repository

import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import com.personalapps.suite.nutrition.feature.api.repository.HistoryRepository
import com.personalapps.suite.nutrition.feature.history.data.dao.HistoryDao
import com.personalapps.suite.nutrition.feature.history.data.entities.HistoryEntryEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(private val historyDao: HistoryDao) : HistoryRepository {
    override fun getAllHistory(): Flow<List<HistoryEntry>> {
        return historyDao.getAllHistory().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getHistoryEntryByDate(date: LocalDate): HistoryEntry? {
        return historyDao.getHistoryEntryByDate(date)?.toDomain()
    }

    override suspend fun insertHistoryEntry(entry: HistoryEntry) {
        historyDao.insertHistoryEntry(entry.toEntity())
    }

    override suspend fun deleteHistoryEntry(entry: HistoryEntry) {
        historyDao.deleteHistoryEntry(entry.toEntity())
    }
}

fun HistoryEntryEntity.toDomain() = HistoryEntry(
    date = date,
    totalCalories = totalCalories,
    totalProtein = totalProtein,
    totalCarbs = totalCarbs,
    totalFat = totalFat,
    goalCalories = goalCalories,
    goalProtein = goalProtein,
    goalCarbs = goalCarbs,
    goalFat = goalFat
)

fun HistoryEntry.toEntity() = HistoryEntryEntity(
    date = date,
    totalCalories = totalCalories,
    totalProtein = totalProtein,
    totalCarbs = totalCarbs,
    totalFat = totalFat,
    goalCalories = goalCalories,
    goalProtein = goalProtein,
    goalCarbs = goalCarbs,
    goalFat = goalFat
)
