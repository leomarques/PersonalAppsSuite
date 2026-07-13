package com.personalapps.suite.workout.feature.workouts.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.workout.feature.api.model.Exercise
import com.personalapps.suite.workout.feature.api.repository.ExerciseRepository
import com.personalapps.suite.workout.feature.api.model.WorkoutSession
import com.personalapps.suite.workout.feature.api.model.WorkoutSet
import com.personalapps.suite.workout.feature.api.repository.WorkoutRepository
import com.personalapps.suite.workout.feature.workouts.domain.usecase.CreateWorkoutSessionUseCase
import com.personalapps.suite.shared.common.Result
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import kotlinx.coroutines.launch

data class WorkoutUiState(
    val sessions: List<WorkoutSession> = emptyList(),
    val sets: List<WorkoutSet> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface WorkoutEffect {
    data class ShowError(val message: String) : WorkoutEffect
    data object WorkoutCreated : WorkoutEffect
    data object WorkoutDeleted : WorkoutEffect
}

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val createWorkoutSessionUseCase: CreateWorkoutSessionUseCase
) : BaseViewModel<WorkoutUiState, WorkoutEffect>(WorkoutUiState()) {

    init {
        viewModelScope.launch {
            workoutRepository.getAllSessions().collect { sessions ->
                updateState { copy(sessions = sessions, isLoading = false) }
            }
        }
        viewModelScope.launch {
            workoutRepository.getAllSets().collect { sets ->
                updateState { copy(sets = sets) }
            }
        }
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                updateState { copy(exercises = exercises) }
            }
        }
    }

    fun createWorkoutSession(name: String, sets: List<WorkoutSet>) {
        if (name.isBlank() || sets.isEmpty()) return
        viewModelScope.launch {
            when (val result = createWorkoutSessionUseCase(name, sets)) {
                is Result.Success -> sendEffect(WorkoutEffect.WorkoutCreated)
                is Result.Error -> sendEffect(WorkoutEffect.ShowError(result.exception.message ?: "Failed to create workout"))
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            try {
                workoutRepository.deleteSession(session)
                sendEffect(WorkoutEffect.WorkoutDeleted)
            } catch (e: Exception) {
                sendEffect(WorkoutEffect.ShowError(e.message ?: "Failed to delete session"))
            }
        }
    }
}
