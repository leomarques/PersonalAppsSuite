package com.personalapps.suite.workout.feature.progress.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import com.personalapps.suite.workout.feature.api.model.Exercise
import com.personalapps.suite.workout.feature.api.repository.ExerciseRepository
import com.personalapps.suite.workout.feature.api.model.WorkoutSession
import com.personalapps.suite.workout.feature.api.model.WorkoutSet
import com.personalapps.suite.workout.feature.api.repository.WorkoutRepository
import com.personalapps.suite.workout.feature.progress.domain.usecase.GetProgressPointsUseCase
import com.personalapps.suite.workout.feature.progress.domain.usecase.ExerciseProgressPoint
import kotlinx.coroutines.launch

data class ProgressUiState(
    val exercises: List<Exercise> = emptyList(),
    val sessions: List<WorkoutSession> = emptyList(),
    val sets: List<WorkoutSet> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface ProgressEffect {
    data class ShowError(val message: String) : ProgressEffect
}

class ProgressViewModel(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val getProgressPointsUseCase: GetProgressPointsUseCase
) : BaseViewModel<ProgressUiState, ProgressEffect>(ProgressUiState()) {

    init {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                updateState { copy(exercises = exercises, isLoading = false) }
            }
        }
        viewModelScope.launch {
            workoutRepository.getAllSessions().collect { sessions ->
                updateState { copy(sessions = sessions) }
            }
        }
        viewModelScope.launch {
            workoutRepository.getAllSets().collect { sets ->
                updateState { copy(sets = sets) }
            }
        }
    }

    fun getProgressPoints(
        exerciseId: Long,
        sessions: List<WorkoutSession>,
        sets: List<WorkoutSet>
    ): List<ExerciseProgressPoint> {
        return getProgressPointsUseCase(exerciseId, sessions, sets)
    }
}
