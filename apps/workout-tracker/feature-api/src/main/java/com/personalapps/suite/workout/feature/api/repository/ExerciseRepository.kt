package com.personalapps.suite.workout.feature.api.repository

import com.personalapps.suite.workout.feature.api.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>>
    suspend fun insertExercise(exercise: Exercise): Long
    suspend fun deleteExercise(exercise: Exercise)
}
