package com.personalapps.suite.cannabis.feature.sessions.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "cannabis_sessions")
data class CannabisSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val startTime: Instant,
    val endTime: Instant? = null
)
