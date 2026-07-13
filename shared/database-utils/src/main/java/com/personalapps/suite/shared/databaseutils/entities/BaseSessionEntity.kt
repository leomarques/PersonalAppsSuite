package com.personalapps.suite.shared.databaseutils.entities

import java.time.Instant

interface BaseSessionEntity {
    val id: Long
    val startTime: Instant
    val endTime: Instant?
}
