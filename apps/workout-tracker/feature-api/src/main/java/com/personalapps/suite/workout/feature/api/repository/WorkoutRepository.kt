package com.personalapps.suite.workout.feature.api.repository

import com.personalapps.suite.workout.feature.api.model.WorkoutSession
import com.personalapps.suite.workout.feature.api.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllSessions(): Flow<List<WorkoutSession>>
    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSet>>
    fun getSetsForExercise(exerciseId: Long): Flow<List<WorkoutSet>>
    fun getAllSets(): Flow<List<WorkoutSet>>
    suspend fun insertSession(session: WorkoutSession): Long
    suspend fun deleteSession(session: WorkoutSession)
    suspend fun insertSet(set: WorkoutSet): Long
    suspend fun deleteSet(set: WorkoutSet)
}
