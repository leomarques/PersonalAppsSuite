package com.personalapps.suite.nutrition.feature.api.repository

import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.shared.common.Result
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun getAllMeals(): Flow<List<Meal>>
    suspend fun insertMeal(meal: Meal): Result<Long>
    suspend fun deleteMeal(meal: Meal): Result<Unit>
}
