package com.personalapps.suite.nutrition.feature.api.model

import java.time.LocalDate

data class HistoryEntry(
    val date: LocalDate,
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val goalCalories: Int,
    val goalProtein: Float,
    val goalCarbs: Float,
    val goalFat: Float
)
