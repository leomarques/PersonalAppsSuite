package com.personalapps.suite.nutrition.feature.api.repository

import com.personalapps.suite.nutrition.feature.api.model.Meal
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun getAllMeals(): Flow<List<Meal>>
    suspend fun insertMeal(meal: Meal): Long
    suspend fun deleteMeal(meal: Meal)
}
