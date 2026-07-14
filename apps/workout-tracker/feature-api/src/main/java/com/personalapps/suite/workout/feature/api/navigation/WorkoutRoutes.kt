package com.personalapps.suite.workout.feature.api.navigation

import com.personalapps.suite.shared.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute : Destination

@Serializable
data object ExercisesRoute : Destination

@Serializable
data object ProgressRoute : Destination
