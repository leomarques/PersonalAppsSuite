package com.personalapps.suite.nutrition.feature.macros.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "macro_goals")
data class MacroGoalEntity(
    @PrimaryKey val id: Int = 1,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)
