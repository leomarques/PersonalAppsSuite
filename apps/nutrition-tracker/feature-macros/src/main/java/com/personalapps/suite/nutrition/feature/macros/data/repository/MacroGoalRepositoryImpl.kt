package com.personalapps.suite.nutrition.feature.macros.data.repository

import com.personalapps.suite.nutrition.feature.macros.data.dao.MacroGoalDao
import com.personalapps.suite.nutrition.feature.macros.data.entities.MacroGoalEntity
import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun MacroGoalEntity.toDomain() = MacroGoal(id = id, calories = calories, protein = protein, carbs = carbs, fat = fat)
fun MacroGoal.toEntity() = MacroGoalEntity(id = id, calories = calories, protein = protein, carbs = carbs, fat = fat)

class MacroGoalRepositoryImpl(private val macroGoalDao: MacroGoalDao) : MacroGoalRepository {
    override fun getMacroGoal(): Flow<MacroGoal?> = macroGoalDao.getMacroGoal().map { it?.toDomain() }
    override suspend fun insertMacroGoal(goal: MacroGoal): Long = macroGoalDao.insertMacroGoal(goal.toEntity())
}
