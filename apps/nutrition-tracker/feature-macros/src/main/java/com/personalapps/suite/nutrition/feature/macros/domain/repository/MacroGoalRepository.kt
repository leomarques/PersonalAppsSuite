package com.personalapps.suite.nutrition.feature.macros.domain.repository

import com.personalapps.suite.nutrition.feature.macros.domain.model.MacroGoal
import kotlinx.coroutines.flow.Flow

interface MacroGoalRepository {
    fun getMacroGoal(): Flow<MacroGoal?>
    suspend fun insertMacroGoal(goal: MacroGoal): Long
}
