package com.personalapps.suite.nutrition.feature.meals.domain.repository

import com.personalapps.suite.nutrition.feature.meals.domain.model.Meal
import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun getMealsBetween(startDate: Instant, endDate: Instant): Flow<List<Meal>>
    fun getAllMeals(): Flow<List<Meal>>
    suspend fun insertMeal(meal: Meal): Long
    suspend fun deleteMeal(meal: Meal)
}
