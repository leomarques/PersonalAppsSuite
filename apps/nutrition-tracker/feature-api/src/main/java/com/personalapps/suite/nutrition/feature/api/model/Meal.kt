package com.personalapps.suite.nutrition.feature.api.model

data class Meal(
    val id: Long = 0,
    val name: String,
    val loggedFoods: List<LoggedFoodPortion>
)
