package com.personalapps.suite.nutrition.feature.api.repository

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.shared.common.Result
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getAllFoods(): Flow<List<Food>>
    suspend fun insertFood(food: Food): Result<Long>
    suspend fun updateFood(food: Food): Result<Unit>
    suspend fun deleteFood(food: Food): Result<Unit>
    suspend fun incrementFrequency(foodId: Long): Result<Unit>
    suspend fun incrementFrequencyByName(name: String): Result<Unit>
}
