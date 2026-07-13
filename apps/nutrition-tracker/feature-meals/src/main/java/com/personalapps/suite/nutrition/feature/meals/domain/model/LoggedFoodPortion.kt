package com.personalapps.suite.nutrition.feature.meals.domain.model

data class LoggedFoodPortion(
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val amountGrams: Float
)
