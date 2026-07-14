package com.personalapps.suite.workout.feature.api.model

import java.time.Instant

data class WorkoutSession(
    val id: Long = 0,
    val name: String,
    val timestamp: Instant,
    val sets: List<WorkoutSet> = emptyList()
)
