package com.personalapps.suite.cannabis.feature.sessions.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "cannabis_logs",
    foreignKeys = [
        ForeignKey(
            entity = CannabisSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class CannabisLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long?,
    val strainName: String,
    val method: String,
    val amountGrams: Float,
    val timestamp: Instant,
    val notes: String
)
