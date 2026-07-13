package com.personalapps.suite.workout.feature.workouts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalapps.suite.workout.feature.exercises.domain.model.Exercise
import com.personalapps.suite.workout.feature.exercises.domain.repository.ExerciseRepository
import com.personalapps.suite.workout.feature.workouts.domain.model.WorkoutSession
import com.personalapps.suite.workout.feature.workouts.domain.model.WorkoutSet
import com.personalapps.suite.workout.feature.workouts.domain.repository.WorkoutRepository
import java.time.Instant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    val sessionsState: StateFlow<List<WorkoutSession>> = workoutRepository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val setsState: StateFlow<List<WorkoutSet>> = workoutRepository.getAllSets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exercisesState: StateFlow<List<Exercise>> = exerciseRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createWorkoutSession(name: String, sets: List<WorkoutSet>) {
        if (name.isBlank() || sets.isEmpty()) return
        viewModelScope.launch {
            val sessionId = workoutRepository.insertSession(
                WorkoutSession(
                    name = name,
                    timestamp = Instant.now()
                )
            )
            sets.forEach { set ->
                workoutRepository.insertSet(
                    set.copy(workoutSessionId = sessionId)
                )
            }
        }
    }

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            workoutRepository.deleteSession(session)
        }
    }
}
