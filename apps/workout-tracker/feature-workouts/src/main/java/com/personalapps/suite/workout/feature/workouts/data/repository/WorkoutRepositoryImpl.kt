package com.personalapps.suite.workout.feature.workouts.data.repository

import com.personalapps.suite.workout.feature.workouts.data.dao.WorkoutDao
import com.personalapps.suite.workout.feature.workouts.data.entities.WorkoutSessionEntity
import com.personalapps.suite.workout.feature.workouts.data.entities.WorkoutSetEntity
import com.personalapps.suite.workout.feature.api.model.WorkoutSession
import com.personalapps.suite.workout.feature.api.model.WorkoutSet
import com.personalapps.suite.workout.feature.api.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

fun WorkoutSessionEntity.toDomain() = WorkoutSession(id = id, name = name, timestamp = timestamp)
fun WorkoutSession.toEntity() = WorkoutSessionEntity(id = id, name = name, timestamp = timestamp)

fun WorkoutSetEntity.toDomain() = WorkoutSet(id = id, workoutSessionId = workoutSessionId, exerciseId = exerciseId, reps = reps, loadKg = loadKg)
fun WorkoutSet.toEntity() = WorkoutSetEntity(id = id, workoutSessionId = workoutSessionId, exerciseId = exerciseId, reps = reps, loadKg = loadKg)

class WorkoutRepositoryImpl(private val workoutDao: WorkoutDao) : WorkoutRepository {
    override fun getAllSessions(): Flow<List<WorkoutSession>> {
        return combine(
            workoutDao.getAllSessions(),
            workoutDao.getAllSets()
        ) { sessions, sets ->
            sessions.map { sessionEntity ->
                val sessionSets = sets
                    .filter { it.workoutSessionId == sessionEntity.id }
                    .map { it.toDomain() }
                sessionEntity.toDomain().copy(sets = sessionSets)
            }
        }
    }
    override fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSet>> = workoutDao.getSetsForSession(sessionId).map { list -> list.map { it.toDomain() } }
    override fun getSetsForExercise(exerciseId: Long): Flow<List<WorkoutSet>> = workoutDao.getSetsForExercise(exerciseId).map { list -> list.map { it.toDomain() } }
    override fun getAllSets(): Flow<List<WorkoutSet>> = workoutDao.getAllSets().map { list -> list.map { it.toDomain() } }
    override suspend fun insertSession(session: WorkoutSession): Long = workoutDao.insertSession(session.toEntity())
    override suspend fun deleteSession(session: WorkoutSession) {
        workoutDao.deleteSession(session.toEntity())
    }
    override suspend fun insertSet(set: WorkoutSet): Long = workoutDao.insertSet(set.toEntity())
    override suspend fun deleteSet(set: WorkoutSet) {
        workoutDao.deleteSet(set.toEntity())
    }
}
