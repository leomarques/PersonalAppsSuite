package com.personalapps.suite.nutrition.feature.meals.data.repository

import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.nutrition.feature.meals.data.dao.MealDao
import com.personalapps.suite.nutrition.feature.meals.data.entities.LoggedFoodPortionEntity
import com.personalapps.suite.nutrition.feature.meals.data.entities.MealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

fun LoggedFoodPortionEntity.toDomain() = LoggedFoodPortion(
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    amountGrams = amountGrams,
    gramsPerServing = gramsPerServing
)

fun LoggedFoodPortion.toEntity() = LoggedFoodPortionEntity(
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    amountGrams = amountGrams,
    gramsPerServing = gramsPerServing
)

fun MealEntity.toDomain(): Meal {
    val portions = try {
        Json.decodeFromString<List<LoggedFoodPortionEntity>>(loggedFoodsJson).map { it.toDomain() }
    } catch (e: Exception) {
        emptyList()
    }
    return Meal(
        id = id,
        name = name,
        loggedFoods = portions
    )
}

fun Meal.toEntity() = MealEntity(
    id = id,
    name = name,
    loggedFoodsJson = Json.encodeToString(loggedFoods.map { it.toEntity() })
)

class MealRepositoryImpl(private val mealDao: MealDao) : MealRepository {
    override fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals().map { list -> list.map { it.toDomain() } }

    override suspend fun insertMeal(meal: Meal): Long = mealDao.insertMeal(meal.toEntity())

    override suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal.toEntity())
    }
}
