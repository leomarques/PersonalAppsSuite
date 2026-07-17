package com.personalapps.suite.nutrition.feature.food.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.shared.common.Result

class AddFoodUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(name: String, calories: Int, protein: Float, carbs: Float, fat: Float): Result<Long> {
        if (name.isBlank()) {
            return Result.Error(IllegalArgumentException("Food name cannot be empty"))
        }
        return repository.insertFood(
            Food(
                name = name,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat
            )
        )
    }
}
