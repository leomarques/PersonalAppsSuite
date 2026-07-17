package com.personalapps.suite.nutrition.feature.food.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.shared.common.Result

class UpdateFoodUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(id: Long, name: String, calories: Int, protein: Float, carbs: Float, fat: Float): Result<Unit> {
        if (name.isBlank()) {
            return Result.Error(IllegalArgumentException("Food name cannot be empty"))
        }
        return repository.updateFood(
            Food(
                id = id,
                name = name,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat
            )
        )
    }
}
