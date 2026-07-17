package com.personalapps.suite.nutrition.feature.macros.data.repository

import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.macros.data.dao.MacroGoalDao
import com.personalapps.suite.nutrition.feature.macros.data.mapper.toDomain
import com.personalapps.suite.nutrition.feature.macros.data.mapper.toEntity
import com.personalapps.suite.shared.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MacroGoalRepositoryImpl(private val macroGoalDao: MacroGoalDao) : MacroGoalRepository {
    override fun getMacroGoal(): Flow<MacroGoal?> = macroGoalDao.getMacroGoal().map { it?.toDomain() }
    
    override suspend fun insertMacroGoal(goal: MacroGoal): Result<Long> = try {
        Result.Success(macroGoalDao.insertMacroGoal(goal.toEntity()))
    } catch (e: Exception) {
        Result.Error(e)
    }
}
