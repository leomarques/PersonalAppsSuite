package com.personalapps.suite.nutrition.feature.history.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "history_entries")
data class HistoryEntryEntity(
    @PrimaryKey val date: LocalDate,
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val goalCalories: Int,
    val goalProtein: Float,
    val goalCarbs: Float,
    val goalFat: Float
)
