package com.personalapps.suite.workout.feature.workouts.domain.usecase

import com.personalapps.suite.workout.feature.api.model.WorkoutSession
import com.personalapps.suite.workout.feature.api.model.WorkoutSet
import com.personalapps.suite.workout.feature.api.repository.WorkoutRepository
import com.personalapps.suite.shared.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeWorkoutRepository : WorkoutRepository {
    private val sessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    private val sets = MutableStateFlow<List<WorkoutSet>>(emptyList())

    override fun getAllSessions(): Flow<List<WorkoutSession>> = sessions
    override fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSet>> = sets
    override fun getSetsForExercise(exerciseId: Long): Flow<List<WorkoutSet>> = sets
    override fun getAllSets(): Flow<List<WorkoutSet>> = sets

    override suspend fun insertSession(session: WorkoutSession): Long {
        val list = sessions.value.toMutableList()
        val newS = session.copy(id = (list.size + 1).toLong())
        list.add(newS)
        sessions.value = list
        return newS.id
    }

    override suspend fun deleteSession(session: WorkoutSession) {}

    override suspend fun insertSet(set: WorkoutSet): Long {
        val list = sets.value.toMutableList()
        val newSet = set.copy(id = (list.size + 1).toLong())
        list.add(newSet)
        sets.value = list
        return newSet.id
    }

    override suspend fun deleteSet(set: WorkoutSet) {}
}

class CreateWorkoutSessionUseCaseTest {
    private val repository = FakeWorkoutRepository()
    private lateinit var useCase: CreateWorkoutSessionUseCase

    @Before
    fun setUp() {
        useCase = CreateWorkoutSessionUseCase(repository)
    }

    @Test
    fun invoke_savesSessionAndCascadeSets() = runTest {
        val sets = listOf(
            WorkoutSet(id = 0L, workoutSessionId = 0L, exerciseId = 1L, reps = 8, loadKg = 60f)
        )
        val result = useCase("Push Day", sets)

        assertTrue(result is Result.Success)
        val sessionId = (result as Result.Success).data
        assertEquals(1L, sessionId)

        val savedSets = repository.getAllSets().first()
        assertEquals(1, savedSets.size)
        assertEquals(1L, savedSets.first().workoutSessionId)
    }
}
