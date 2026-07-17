package com.personalapps.suite.nutrition.feature.history.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.personalapps.suite.nutrition.feature.history.data.entities.HistoryEntryEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_entries ORDER BY date DESC")
    fun getAllHistory(): Flow<List<HistoryEntryEntity>>

    @Query("SELECT * FROM history_entries WHERE date = :date")
    suspend fun getHistoryEntryByDate(date: LocalDate): HistoryEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryEntry(entry: HistoryEntryEntity)

    @Delete
    suspend fun deleteHistoryEntry(entry: HistoryEntryEntity)
}
