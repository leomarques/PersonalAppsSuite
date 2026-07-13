package com.personalapps.suite.workout.feature.workouts.domain.model

import java.time.Instant

data class WorkoutSession(
    val id: Long = 0,
    val name: String,
    val timestamp: Instant
)
