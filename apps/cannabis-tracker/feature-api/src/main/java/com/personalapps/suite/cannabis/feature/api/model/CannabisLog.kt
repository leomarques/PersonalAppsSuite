package com.personalapps.suite.cannabis.feature.api.model

import java.time.Instant

data class CannabisLog(
    val id: Long = 0,
    val sessionId: Long?,
    val strainName: String,
    val method: String,
    val amountGrams: Float,
    val timestamp: Instant,
    val notes: String
)
