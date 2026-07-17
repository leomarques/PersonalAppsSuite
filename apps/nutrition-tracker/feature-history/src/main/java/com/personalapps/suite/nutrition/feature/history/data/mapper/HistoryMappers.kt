package com.personalapps.suite.nutrition.feature.history.data.mapper

import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import com.personalapps.suite.nutrition.feature.history.data.entities.HistoryEntryEntity

fun HistoryEntryEntity.toDomain() = HistoryEntry(
    date = date,
    totalCalories = totalCalories,
    totalProtein = totalProtein,
    totalCarbs = totalCarbs,
    totalFat = totalFat,
    goalCalories = goalCalories,
    goalProtein = goalProtein,
    goalCarbs = goalCarbs,
    goalFat = goalFat
)

fun HistoryEntry.toEntity() = HistoryEntryEntity(
    date = date,
    totalCalories = totalCalories,
    totalProtein = totalProtein,
    totalCarbs = totalCarbs,
    totalFat = totalFat,
    goalCalories = goalCalories,
    goalProtein = goalProtein,
    goalCarbs = goalCarbs,
    goalFat = goalFat
)
