package com.personalapps.suite.nutrition.feature.meals.domain.model

import com.personalapps.suite.nutrition.feature.meals.domain.model.LoggedFoodPortion
import java.time.Instant

data class Meal(
    val id: Long = 0,
    val name: String,
    val timestamp: Instant,
    val loggedFoods: List<LoggedFoodPortion>
)
