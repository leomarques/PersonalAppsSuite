package com.personalapps.suite.nutrition.feature.meals.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.common.Result
import java.time.Instant

class LogMealUseCase(
    private val mealRepository: MealRepository,
    private val foodRepository: com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
) {
    
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
            
            // Increment frequency for each food portion logged
            portions.forEach { portion ->
                foodRepository.incrementFrequencyByName(portion.name)
            }
            
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun logSingleFoodPortion(food: Food, amountGrams: Float): Result<Long> {
        if (amountGrams <= 0f) {
            return Result.Error(IllegalArgumentException("Invalid food amount"))
        }
        return try {
            val factor = amountGrams / food.gramsPerServing
            val portion = LoggedFoodPortion(
                name = food.name,
                calories = (food.calories * factor).toInt(),
                protein = food.protein * factor,
                carbs = food.carbs * factor,
                fat = food.fat * factor,
                amountGrams = amountGrams,
                gramsPerServing = food.gramsPerServing
            )
            val meal = Meal(
                name = food.name,
                timestamp = Instant.now(),
                loggedFoods = listOf(portion)
            )
            val id = mealRepository.insertMeal(meal)
            
            // Increment frequency for the food logged
            if (food.id != 0L) {
                foodRepository.incrementFrequency(food.id)
            } else {
                foodRepository.incrementFrequencyByName(food.name)
            }
            
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
