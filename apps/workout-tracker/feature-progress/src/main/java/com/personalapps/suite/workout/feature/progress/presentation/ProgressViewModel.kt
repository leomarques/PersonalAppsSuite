package com.personalapps.suite.workout.feature.progress.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalapps.suite.shared.common.DateUtils
import com.personalapps.suite.workout.feature.exercises.domain.model.Exercise
import com.personalapps.suite.workout.feature.exercises.domain.repository.ExerciseRepository
import com.personalapps.suite.workout.feature.workouts.domain.model.WorkoutSession
import com.personalapps.suite.workout.feature.workouts.domain.model.WorkoutSet
import com.personalapps.suite.workout.feature.workouts.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class ExerciseProgressPoint(
    val dateMillis: Long,
    val formattedDate: String,
    val maxLoadKg: Float,
    val totalVolume: Float
)

class ProgressViewModel(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    val exercisesState: StateFlow<List<Exercise>> = exerciseRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessionsState: StateFlow<List<WorkoutSession>> = workoutRepository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val setsState: StateFlow<List<WorkoutSet>> = workoutRepository.getAllSets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getProgressPoints(
        exerciseId: Long,
        sessions: List<WorkoutSession>,
        sets: List<WorkoutSet>
    ): List<ExerciseProgressPoint> {
        val exerciseSets = sets.filter { it.exerciseId == exerciseId }
        val sessionsMap = sessions.associateBy { it.id }

        return exerciseSets
            .mapNotNull { set ->
                val session = sessionsMap[set.workoutSessionId] ?: return@mapNotNull null
                set to session
            }
            .groupBy { it.second.id }
            .map { (_, pairs) ->
                val session = pairs.first().second
                val maxLoad = pairs.maxOf { it.first.loadKg }
                val volume = pairs.sumOf { (it.first.loadKg * it.first.reps).toDouble() }.toFloat()
                ExerciseProgressPoint(
                    dateMillis = session.timestamp.toEpochMilli(),
                    formattedDate = DateUtils.formatDateOnly(session.timestamp),
                    maxLoadKg = maxLoad,
                    totalVolume = volume
                )
            }
            .sortedBy { it.dateMillis }
    }
}
