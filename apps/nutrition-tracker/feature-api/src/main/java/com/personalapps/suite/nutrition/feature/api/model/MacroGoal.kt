package com.personalapps.suite.nutrition.feature.api.model

data class MacroGoal(
    val id: Int = 1,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int
)
