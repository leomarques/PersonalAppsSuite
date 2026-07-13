package com.personalapps.suite.nutrition.feature.api.model

data class MacroGoal(
    val id: Int = 1,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)
