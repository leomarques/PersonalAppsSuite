package com.personalapps.suite.nutrition.feature.meals.data.repository

import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.nutrition.feature.meals.data.dao.MealDao
import com.personalapps.suite.nutrition.feature.meals.data.mapper.toDomain
import com.personalapps.suite.nutrition.feature.meals.data.mapper.toEntity
import com.personalapps.suite.shared.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealRepositoryImpl(private val mealDao: MealDao) : MealRepository {
    override fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals().map { list -> list.map { it.toDomain() } }

    override suspend fun insertMeal(meal: Meal): Result<Long> = try {
        Result.Success(mealDao.insertMeal(meal.toEntity()))
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun deleteMeal(meal: Meal): Result<Unit> = try {
        mealDao.deleteMeal(meal.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
