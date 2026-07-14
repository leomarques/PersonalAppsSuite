package com.personalapps.suite.workout.feature.progress.domain.usecase

import com.personalapps.suite.shared.common.DateUtils
import com.personalapps.suite.workout.feature.api.model.WorkoutSession
import com.personalapps.suite.workout.feature.api.model.WorkoutSet

data class ExerciseProgressPoint(
    val dateMillis: Long,
    val formattedDate: String,
    val maxLoadKg: Float,
    val totalVolume: Float
)

class GetProgressPointsUseCase {
    
    operator fun invoke(
        exerciseId: Long,
        sessions: List<WorkoutSession>
    ): List<ExerciseProgressPoint> {
        return sessions
            .mapNotNull { session ->
                val exerciseSets = session.sets.filter { it.exerciseId == exerciseId }
                if (exerciseSets.isEmpty()) return@mapNotNull null
                val maxLoad = exerciseSets.maxOf { it.loadKg }
                val volume = exerciseSets.sumOf { (it.loadKg * it.reps).toDouble() }.toFloat()
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
