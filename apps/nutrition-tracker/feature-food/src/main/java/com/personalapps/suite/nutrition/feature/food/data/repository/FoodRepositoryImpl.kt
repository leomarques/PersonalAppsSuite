package com.personalapps.suite.nutrition.feature.food.data.repository

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.food.data.dao.FoodDao
import com.personalapps.suite.nutrition.feature.food.data.mapper.toDomain
import com.personalapps.suite.nutrition.feature.food.data.mapper.toEntity
import com.personalapps.suite.shared.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodRepositoryImpl(private val foodDao: FoodDao) : FoodRepository {
    override fun getAllFoods(): Flow<List<Food>> = foodDao.getAllFoods().map { list -> list.map { it.toDomain() } }
    
    override suspend fun insertFood(food: Food): Result<Long> = try {
        Result.Success(foodDao.insertFood(food.toEntity()))
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateFood(food: Food): Result<Unit> = try {
        foodDao.updateFood(food.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun deleteFood(food: Food): Result<Unit> = try {
        foodDao.deleteFood(food.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun incrementFrequency(foodId: Long): Result<Unit> = try {
        foodDao.incrementFrequency(foodId)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun incrementFrequencyByName(name: String): Result<Unit> = try {
        foodDao.incrementFrequencyByName(name)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
