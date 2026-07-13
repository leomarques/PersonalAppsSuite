package com.personalapps.suite.nutrition.feature.api.repository

import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import kotlinx.coroutines.flow.Flow

interface MacroGoalRepository {
    fun getMacroGoal(): Flow<MacroGoal?>
    suspend fun insertMacroGoal(goal: MacroGoal): Long
}
