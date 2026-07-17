package com.personalapps.suite.nutrition.feature.macros.data.mapper

import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.macros.data.entities.MacroGoalEntity

fun MacroGoalEntity.toDomain() = MacroGoal(
    id = id,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat
)

fun MacroGoal.toEntity() = MacroGoalEntity(
    id = id,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat
)
