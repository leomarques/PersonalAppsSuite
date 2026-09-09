package com.personalapps.suite.cannabis.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageDao {

    @Insert
    suspend fun insert(usage: UsageEntity)

    @Delete
    suspend fun delete(usage: UsageEntity)

    @Query("SELECT * FROM usage WHERE dayStart = :dayStart ORDER BY timestamp ASC")
    fun getForDayStart(dayStart: Long): Flow<List<UsageEntity>>

    @Query("""
        SELECT dayStart, COUNT(*) as count
        FROM usage
        WHERE dayStart > 0
        GROUP BY dayStart
        ORDER BY dayStart DESC
    """)
    fun getDailyCounts(): Flow<List<DailyCount>>

    @Query("DELETE FROM usage WHERE dayStart = :dayStart")
    suspend fun deleteByDayStart(dayStart: Long)

    @Query("UPDATE usage SET timestamp = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: Long, timestamp: Long)
}

data class DailyCount(
    val dayStart: Long,
    val count: Int
)
