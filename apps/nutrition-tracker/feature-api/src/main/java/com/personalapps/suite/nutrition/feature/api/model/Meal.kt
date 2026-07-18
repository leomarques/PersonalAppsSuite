package com.personalapps.suite.nutrition.feature.api.model

import java.time.Instant

data class Meal(
    val id: Long = 0,
    val name: String,
    val loggedFoods: List<LoggedFoodPortion>,
    val createdAt: Instant = Instant.EPOCH
)
