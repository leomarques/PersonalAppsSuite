package com.personalapps.suite.workout.feature.workouts.domain.usecase

import com.personalapps.suite.workout.feature.api.model.WorkoutSession
import com.personalapps.suite.workout.feature.api.model.WorkoutSet
import com.personalapps.suite.workout.feature.api.repository.WorkoutRepository
import com.personalapps.suite.shared.common.Result
import java.time.Instant

class CreateWorkoutSessionUseCase(private val workoutRepository: WorkoutRepository) {
    
    suspend operator fun invoke(name: String, sets: List<WorkoutSet>): Result<Long> {
        if (name.isBlank() || sets.isEmpty()) {
            return Result.Error(IllegalArgumentException("Workout name cannot be blank and sets cannot be empty"))
        }
        return try {
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
            Result.Success(sessionId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
