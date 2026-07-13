package com.personalapps.suite.nutrition.feature.food.domain.model

data class Food(
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)
