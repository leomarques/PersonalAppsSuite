package com.personalapps.suite.workout.feature.progress.domain.usecase

import com.personalapps.suite.workout.feature.api.model.WorkoutSession
import com.personalapps.suite.workout.feature.api.model.WorkoutSet
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetProgressPointsUseCaseTest {
    private lateinit var useCase: GetProgressPointsUseCase

    @Before
    fun setUp() {
        useCase = GetProgressPointsUseCase()
    }

    @Test
    fun invoke_aggregatesSetsBySession() {
        val exerciseId = 1L
        val sessions = listOf(
            WorkoutSession(id = 1L, name = "Legs", timestamp = Instant.parse("2026-07-01T10:00:00Z"))
        )
        val sets = listOf(
            WorkoutSet(id = 1L, workoutSessionId = 1L, exerciseId = 1L, reps = 10, loadKg = 100f),
            WorkoutSet(id = 2L, workoutSessionId = 1L, exerciseId = 1L, reps = 8, loadKg = 110f)
        )

        val points = useCase(exerciseId, sessions, sets)
        assertEquals(1, points.size)
        assertEquals(110f, points.first().maxLoadKg)
        // Volume: (100 * 10) + (110 * 8) = 1000 + 880 = 1880f
        assertEquals(1880f, points.first().totalVolume)
    }
}
