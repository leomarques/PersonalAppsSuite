package com.personalapps.suite.workout.feature.progress.presentation

import com.personalapps.suite.shared.testing.MainDispatcherRule
import com.personalapps.suite.workout.feature.api.model.Exercise
import com.personalapps.suite.workout.feature.api.repository.ExerciseRepository
import com.personalapps.suite.workout.feature.api.model.WorkoutSession
import com.personalapps.suite.workout.feature.api.model.WorkoutSet
import com.personalapps.suite.workout.feature.api.repository.WorkoutRepository
import com.personalapps.suite.workout.feature.progress.domain.usecase.GetProgressPointsUseCase
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    override fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSet>> = sets
    override fun getSetsForExercise(exerciseId: Long): Flow<List<WorkoutSet>> = sets
    override fun getAllSets(): Flow<List<WorkoutSet>> = sets
    override suspend fun insertSession(session: WorkoutSession): Long = 0L
    override suspend fun deleteSession(session: WorkoutSession) {}
    override suspend fun insertSet(set: WorkoutSet): Long = 0L
    override suspend fun deleteSet(set: WorkoutSet) {}
}

class FakeExerciseRepository : ExerciseRepository {
    override fun getAllExercises(): Flow<List<Exercise>> = MutableStateFlow(emptyList())
    override suspend fun insertExercise(exercise: Exercise): Long = 0L
    override suspend fun deleteExercise(exercise: Exercise) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val workoutRepository = FakeWorkoutRepository()
    private val exerciseRepository = FakeExerciseRepository()
    private lateinit var getProgressPointsUseCase: GetProgressPointsUseCase
    private lateinit var viewModel: ProgressViewModel

    @Before
    fun setUp() {
        getProgressPointsUseCase = GetProgressPointsUseCase()
        viewModel = ProgressViewModel(workoutRepository, exerciseRepository, getProgressPointsUseCase)
    }

    @Test
    fun getProgressPoints_invokesUseCase() {
        val exerciseId = 1L
        val sessions = listOf(WorkoutSession(id = 1L, name = "Workout", timestamp = Instant.now()))
        val sets = listOf(WorkoutSet(id = 1L, workoutSessionId = 1L, exerciseId = 1L, reps = 10, loadKg = 50f))

        val points = viewModel.getProgressPoints(exerciseId, sessions, sets)
        assertEquals(1, points.size)
        assertEquals(50f, points.first().maxLoadKg)
    }
}
