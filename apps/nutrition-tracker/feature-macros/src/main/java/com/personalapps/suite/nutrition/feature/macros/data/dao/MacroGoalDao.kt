package com.personalapps.suite.nutrition.feature.macros.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.personalapps.suite.nutrition.feature.macros.data.entities.MacroGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MacroGoalDao {
    @Query("SELECT * FROM macro_goals WHERE id = 1 LIMIT 1")
    fun getMacroGoal(): Flow<MacroGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMacroGoal(goal: MacroGoalEntity): Long
}
