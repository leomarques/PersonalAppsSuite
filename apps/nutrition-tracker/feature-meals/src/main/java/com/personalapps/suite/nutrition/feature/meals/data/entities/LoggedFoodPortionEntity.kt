package com.personalapps.suite.nutrition.feature.meals.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class LoggedFoodPortionEntity(
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val amountGrams: Float,
    val gramsPerServing: Float = 100f
)
