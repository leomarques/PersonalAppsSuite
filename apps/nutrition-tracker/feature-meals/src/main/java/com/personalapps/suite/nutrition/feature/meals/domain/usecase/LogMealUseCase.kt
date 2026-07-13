package com.personalapps.suite.nutrition.feature.meals.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.common.Result
import java.time.Instant

class LogMealUseCase(private val mealRepository: MealRepository) {
    
    suspend operator fun invoke(name: String, portions: List<LoggedFoodPortion>): Result<Long> {
        if (name.isBlank() || portions.isEmpty()) {
            return Result.Error(IllegalArgumentException("Meal name cannot be blank and portions cannot be empty"))
        }
        return try {
            val meal = Meal(
                name = name,
                timestamp = Instant.now(),
                loggedFoods = portions
            )
            val id = mealRepository.insertMeal(meal)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun logSingleFoodPortion(food: Food, amountGrams: Float, mealType: String): Result<Long> {
        if (amountGrams <= 0f || mealType.isBlank()) {
            return Result.Error(IllegalArgumentException("Invalid food amount or meal type"))
        }
        return try {
            val factor = amountGrams / 100f
            val portion = LoggedFoodPortion(
                name = food.name,
                calories = (food.calories * factor).toInt(),
                protein = food.protein * factor,
                carbs = food.carbs * factor,
                fat = food.fat * factor,
                amountGrams = amountGrams
            )
            val meal = Meal(
                name = mealType,
                timestamp = Instant.now(),
                loggedFoods = listOf(portion)
            )
            val id = mealRepository.insertMeal(meal)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
