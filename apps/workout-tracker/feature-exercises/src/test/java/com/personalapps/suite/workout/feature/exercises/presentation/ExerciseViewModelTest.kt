package com.personalapps.suite.workout.feature.exercises.presentation

import com.personalapps.suite.shared.testing.MainDispatcherRule
import com.personalapps.suite.workout.feature.exercises.domain.model.Exercise
import com.personalapps.suite.workout.feature.exercises.domain.repository.ExerciseRepository
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

class FakeExerciseRepository : ExerciseRepository {
    private val exercises = MutableStateFlow<List<Exercise>>(emptyList())

    override fun getAllExercises(): Flow<List<Exercise>> = exercises

    override suspend fun insertExercise(exercise: Exercise): Long {
        val list = exercises.value.toMutableList()
        val newEx = exercise.copy(id = (list.size + 1).toLong())
        list.add(newEx)
        exercises.value = list
        return newEx.id
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        val list = exercises.value.toMutableList()
        list.remove(exercise)
        exercises.value = list
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeExerciseRepository()
    private lateinit var viewModel: ExerciseViewModel

    @Before
    fun setUp() {
        viewModel = ExerciseViewModel(repository)
    }

    @Test
    fun addExercise_insertsIntoRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.exercisesState.collect {}
        }

        viewModel.addExercise("Bench Press", "Chest")
        runCurrent()

        val list = viewModel.exercisesState.value
        assertEquals(1, list.size)
        assertEquals("Bench Press", list.first().name)
        assertEquals("Chest", list.first().category)
    }

    @Test
    fun deleteExercise_removesFromRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.exercisesState.collect {}
        }

        viewModel.addExercise("Bench Press", "Chest")
        runCurrent()

        val added = repository.getAllExercises().first().first()
        viewModel.deleteExercise(added)
        runCurrent()

        val list = viewModel.exercisesState.value
        assertEquals(0, list.size)
    }
}
