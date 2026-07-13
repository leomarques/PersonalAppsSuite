package com.personalapps.suite.nutrition.feature.meals.data.repository

import com.personalapps.suite.nutrition.feature.meals.data.dao.MealDao
import com.personalapps.suite.nutrition.feature.meals.data.entities.LoggedFoodPortionEntity
import com.personalapps.suite.nutrition.feature.meals.data.entities.MealEntity
import com.personalapps.suite.nutrition.feature.meals.domain.model.Meal
import com.personalapps.suite.nutrition.feature.meals.domain.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.meals.domain.repository.MealRepository
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun LoggedFoodPortionEntity.toDomain() = LoggedFoodPortion(
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    amountGrams = amountGrams
)

fun LoggedFoodPortion.toEntity() = LoggedFoodPortionEntity(
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    amountGrams = amountGrams
)

fun MealEntity.toDomain(): Meal {
    val portions = try {
        Json.decodeFromString<List<LoggedFoodPortionEntity>>(loggedFoodsJson).map { it.toDomain() }
    } catch (e: Exception) {
        emptyList()
    }
    return Meal(id = id, name = name, timestamp = timestamp, loggedFoods = portions)
}

fun Meal.toEntity() = MealEntity(
    id = id,
    name = name,
    timestamp = timestamp,
    loggedFoodsJson = Json.encodeToString(loggedFoods.map { it.toEntity() })
)

class MealRepositoryImpl(private val mealDao: MealDao) : MealRepository {
    override fun getMealsBetween(startDate: Instant, endDate: Instant): Flow<List<Meal>> =
        mealDao.getMealsBetween(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals().map { list -> list.map { it.toDomain() } }

    override suspend fun insertMeal(meal: Meal): Long = mealDao.insertMeal(meal.toEntity())

    override suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal.toEntity())
    }
}
