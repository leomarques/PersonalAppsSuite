package com.personalapps.suite.cannabis.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage")
data class UsageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dayStart: Long = 0L
)
