package com.personalapps.suite.workout.feature.exercises.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.workout.feature.api.model.Exercise
import com.personalapps.suite.workout.feature.api.repository.ExerciseRepository
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import kotlinx.coroutines.launch

data class ExerciseUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface ExerciseEffect {
    data class ShowError(val message: String) : ExerciseEffect
    data object ExerciseAdded : ExerciseEffect
    data object ExerciseDeleted : ExerciseEffect
}

class ExerciseViewModel(private val repository: ExerciseRepository) : BaseViewModel<ExerciseUiState, ExerciseEffect>(ExerciseUiState()) {

    init {
        viewModelScope.launch {
            repository.getAllExercises().collect { exercises ->
                updateState { copy(exercises = exercises, isLoading = false) }
            }
        }
    }

    fun addExercise(name: String, category: String) {
        if (name.isBlank() || category.isBlank()) return
        viewModelScope.launch {
            try {
                repository.insertExercise(
                    Exercise(
                        name = name,
                        category = category
                    )
                )
                sendEffect(ExerciseEffect.ExerciseAdded)
            } catch (e: Exception) {
                sendEffect(ExerciseEffect.ShowError(e.message ?: "Failed to add exercise"))
            }
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            try {
                repository.deleteExercise(exercise)
                sendEffect(ExerciseEffect.ExerciseDeleted)
            } catch (e: Exception) {
                sendEffect(ExerciseEffect.ShowError(e.message ?: "Failed to delete exercise"))
            }
        }
    }
}
