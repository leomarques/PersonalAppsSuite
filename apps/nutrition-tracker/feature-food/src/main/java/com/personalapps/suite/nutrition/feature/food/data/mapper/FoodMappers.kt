package com.personalapps.suite.nutrition.feature.food.data.mapper

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.food.data.entities.FoodEntity

fun FoodEntity.toDomain(): Food = Food(
    id = id,
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    gramsPerServing = gramsPerServing,
    frequency = frequency
)

fun Food.toEntity(): FoodEntity = FoodEntity(
    id = id,
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    gramsPerServing = gramsPerServing,
    frequency = frequency
)
