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
