package com.personalapps.suite.nutrition.feature.api.repository

import com.personalapps.suite.nutrition.feature.api.model.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getAllFoods(): Flow<List<Food>>
    suspend fun insertFood(food: Food): Long
    suspend fun updateFood(food: Food)
    suspend fun deleteFood(food: Food)
    suspend fun incrementFrequency(foodId: Long)
    suspend fun incrementFrequencyByName(name: String)
}
