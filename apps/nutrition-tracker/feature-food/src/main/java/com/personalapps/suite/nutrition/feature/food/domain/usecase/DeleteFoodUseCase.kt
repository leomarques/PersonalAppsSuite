package com.personalapps.suite.nutrition.feature.food.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.shared.common.Result

class DeleteFoodUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(food: Food): Result<Unit> {
        return repository.deleteFood(food)
    }
}
