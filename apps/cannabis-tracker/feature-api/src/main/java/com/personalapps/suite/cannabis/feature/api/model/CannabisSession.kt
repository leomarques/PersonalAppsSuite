package com.personalapps.suite.cannabis.feature.api.model

import java.time.Instant

data class CannabisSession(
    val id: Long = 0,
    val title: String,
    val startTime: Instant,
    val endTime: Instant? = null
)
