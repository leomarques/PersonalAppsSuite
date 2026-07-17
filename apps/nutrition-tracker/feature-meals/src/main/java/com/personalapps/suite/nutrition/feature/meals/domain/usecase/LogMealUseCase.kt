package com.personalapps.suite.nutrition.feature.meals.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.common.Result
import com.personalapps.suite.shared.databaseutils.TransactionProvider

class LogMealUseCase(
    private val mealRepository: MealRepository,
    private val foodRepository: com.personalapps.suite.nutrition.feature.api.repository.FoodRepository,
    private val transactionProvider: TransactionProvider
) {
    
    suspend operator fun invoke(name: String, portions: List<LoggedFoodPortion>): Result<Long> {
        if (name.isBlank() || portions.isEmpty()) {
            return Result.Error(IllegalArgumentException("Meal name cannot be blank and portions cannot be empty"))
        }
        return try {
            transactionProvider.runInTransaction {
                val meal = Meal(
                    name = name,
                    loggedFoods = portions
                )
                val idResult = mealRepository.insertMeal(meal)
                val id = (idResult as Result.Success).data
                
                // Increment frequency for each food portion logged
                portions.forEach { portion ->
                    foodRepository.incrementFrequencyByName(portion.name)
                }
                id
            }.let { Result.Success(it) }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun logSingleFoodPortion(food: Food, amountGrams: Float): Result<Long> {
        if (amountGrams <= 0f) {
            return Result.Error(IllegalArgumentException("Invalid food amount"))
        }
        return try {
            transactionProvider.runInTransaction {
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
                    loggedFoods = listOf(portion)
                )
                val idResult = mealRepository.insertMeal(meal)
                val id = (idResult as Result.Success).data
                
                // Increment frequency for the food logged
                if (food.id != 0L) {
                    foodRepository.incrementFrequency(food.id)
                } else {
                    foodRepository.incrementFrequencyByName(food.name)
                }
                id
            }.let { Result.Success(it) }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
