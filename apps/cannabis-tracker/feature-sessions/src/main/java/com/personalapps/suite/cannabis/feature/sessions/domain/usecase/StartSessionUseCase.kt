package com.personalapps.suite.cannabis.feature.sessions.domain.usecase

import com.personalapps.suite.cannabis.feature.api.model.CannabisSession
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import com.personalapps.suite.shared.common.Result
import java.time.Instant

class StartSessionUseCase(private val repository: SessionsRepository) {
    suspend operator fun invoke(title: String): Result<Long> {
        if (title.isBlank()) {
            return Result.Error(IllegalArgumentException("Title cannot be blank"))
        }
        return try {
            val session = CannabisSession(
                title = title,
                startTime = Instant.now()
            )
            val id = repository.insertSession(session)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
