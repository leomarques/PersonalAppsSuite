package com.personalapps.suite.workout.feature.workouts.presentation

import com.personalapps.suite.shared.testing.MainDispatcherRule
import com.personalapps.suite.workout.feature.exercises.domain.model.Exercise
import com.personalapps.suite.workout.feature.exercises.domain.repository.ExerciseRepository
import com.personalapps.suite.workout.feature.workouts.domain.model.WorkoutSession
import com.personalapps.suite.workout.feature.workouts.domain.model.WorkoutSet
import com.personalapps.suite.workout.feature.workouts.domain.repository.WorkoutRepository
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FakeWorkoutRepository : WorkoutRepository {
    private val sessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    private val sets = MutableStateFlow<List<WorkoutSet>>(emptyList())

    override fun getAllSessions(): Flow<List<WorkoutSession>> = sessions

    override fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSet>> {
        return MutableStateFlow(sets.value.filter { it.workoutSessionId == sessionId })
    }

    override fun getSetsForExercise(exerciseId: Long): Flow<List<WorkoutSet>> {
        return MutableStateFlow(sets.value.filter { it.exerciseId == exerciseId })
    }

    override fun getAllSets(): Flow<List<WorkoutSet>> = sets

    override suspend fun insertSession(session: WorkoutSession): Long {
        val list = sessions.value.toMutableList()
        val newSess = session.copy(id = (list.size + 1).toLong())
        list.add(newSess)
        sessions.value = list
        return newSess.id
    }

    override suspend fun deleteSession(session: WorkoutSession) {
        val list = sessions.value.toMutableList()
        list.remove(session)
        sessions.value = list
        // Cascade delete sets in fake
        val setsList = sets.value.toMutableList()
        setsList.removeAll { it.workoutSessionId == session.id }
        sets.value = setsList
    }

    override suspend fun insertSet(set: WorkoutSet): Long {
        val list = sets.value.toMutableList()
        val newSet = set.copy(id = (list.size + 1).toLong())
        list.add(newSet)
        sets.value = list
        return newSet.id
    }

    override suspend fun deleteSet(set: WorkoutSet) {
        val list = sets.value.toMutableList()
        list.remove(set)
        sets.value = list
    }
}

class FakeExerciseRepository : ExerciseRepository {
    override fun getAllExercises(): Flow<List<Exercise>> = MutableStateFlow(emptyList())
    override suspend fun insertExercise(exercise: Exercise): Long = 0L
    override suspend fun deleteExercise(exercise: Exercise) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val workoutRepository = FakeWorkoutRepository()
    private val exerciseRepository = FakeExerciseRepository()
    private lateinit var viewModel: WorkoutViewModel

    @Before
    fun setUp() {
        viewModel = WorkoutViewModel(workoutRepository, exerciseRepository)
    }

    @Test
    fun createWorkoutSession_savesSessionAndSets() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.sessionsState.collect {}
            viewModel.setsState.collect {}
        }

        val setsList = listOf(
            WorkoutSet(workoutSessionId = 0L, exerciseId = 1L, reps = 10, loadKg = 50f),
            WorkoutSet(workoutSessionId = 0L, exerciseId = 2L, reps = 12, loadKg = 15f)
        )

        viewModel.createWorkoutSession("Leg Day", setsList)
        runCurrent()

        val loggedSessions = workoutRepository.getAllSessions().first()
        assertEquals(1, loggedSessions.size)
        assertEquals("Leg Day", loggedSessions.first().name)

        val loggedSets = workoutRepository.getAllSets().first()
        assertEquals(2, loggedSets.size)
        assertEquals(1L, loggedSets[0].workoutSessionId)
        assertEquals(50f, loggedSets[0].loadKg)
    }

    @Test
    fun deleteSession_removesSessionAndCascadeSets() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.sessionsState.collect {}
            viewModel.setsState.collect {}
        }

        val setsList = listOf(
            WorkoutSet(workoutSessionId = 0L, exerciseId = 1L, reps = 10, loadKg = 50f)
        )

        viewModel.createWorkoutSession("Leg Day", setsList)
        runCurrent()

        val addedSession = workoutRepository.getAllSessions().first().first()
        viewModel.deleteSession(addedSession)
        runCurrent()

        val loggedSessions = workoutRepository.getAllSessions().first()
        assertEquals(0, loggedSessions.size)

        val loggedSets = workoutRepository.getAllSets().first()
        assertEquals(0, loggedSets.size)
    }
}
