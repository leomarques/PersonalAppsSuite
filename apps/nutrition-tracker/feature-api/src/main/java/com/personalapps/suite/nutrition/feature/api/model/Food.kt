package com.personalapps.suite.nutrition.feature.api.model

data class Food(
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val gramsPerServing: Float = 100f,
    val frequency: Int = 0
)
