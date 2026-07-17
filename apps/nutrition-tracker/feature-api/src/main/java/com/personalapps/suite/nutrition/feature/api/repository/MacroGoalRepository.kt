package com.personalapps.suite.nutrition.feature.api.repository

import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.shared.common.Result
import kotlinx.coroutines.flow.Flow

interface MacroGoalRepository {
    fun getMacroGoal(): Flow<MacroGoal?>
    suspend fun insertMacroGoal(goal: MacroGoal): Result<Long>
}
