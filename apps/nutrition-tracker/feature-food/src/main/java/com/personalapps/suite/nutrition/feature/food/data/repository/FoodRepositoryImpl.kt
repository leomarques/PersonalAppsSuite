package com.personalapps.suite.nutrition.feature.food.data.repository

import com.personalapps.suite.nutrition.feature.food.data.dao.FoodDao
import com.personalapps.suite.nutrition.feature.food.data.entities.FoodEntity
import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun FoodEntity.toDomain() = Food(id = id, name = name, calories = calories, protein = protein, carbs = carbs, fat = fat, gramsPerServing = gramsPerServing)
fun Food.toEntity() = FoodEntity(id = id, name = name, calories = calories, protein = protein, carbs = carbs, fat = fat, gramsPerServing = gramsPerServing)

class FoodRepositoryImpl(private val foodDao: FoodDao) : FoodRepository {
    override fun getAllFoods(): Flow<List<Food>> = foodDao.getAllFoods().map { list -> list.map { it.toDomain() } }
    override suspend fun insertFood(food: Food): Long = foodDao.insertFood(food.toEntity())
    override suspend fun updateFood(food: Food) {
        foodDao.updateFood(food.toEntity())
    }
    override suspend fun deleteFood(food: Food) {
        foodDao.deleteFood(food.toEntity())
    }
}
