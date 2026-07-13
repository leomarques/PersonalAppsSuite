package com.personalapps.suite.workout.feature.api.model

data class WorkoutSet(
    val id: Long = 0,
    val workoutSessionId: Long,
    val exerciseId: Long,
    val reps: Int,
    val loadKg: Float
)
