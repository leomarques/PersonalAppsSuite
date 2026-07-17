package com.personalapps.suite.nutrition.feature.macros.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.shared.common.Result

class SaveMacroGoalUseCase(private val repository: MacroGoalRepository) {
    suspend operator fun invoke(calories: Int, protein: Float, carbs: Float, fat: Float): Result<Long> {
        if (calories <= 0) {
            return Result.Error(IllegalArgumentException("Calories must be greater than 0"))
        }
        return repository.insertMacroGoal(
            MacroGoal(
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat
            )
        )
    }
}
