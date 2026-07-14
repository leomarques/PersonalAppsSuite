package com.personalapps.suite.nutrition.feature.history.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import com.personalapps.suite.nutrition.feature.api.repository.HistoryRepository
import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class DashboardUiState(
    val meals: List<Meal> = emptyList(),
    val goal: MacroGoal? = null,
    val history: List<HistoryEntry> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface HistoryEffect {
    data class ShowError(val message: String) : HistoryEffect
    data object DayStarted : HistoryEffect
}

class HistoryViewModel(
    private val mealRepository: MealRepository,
    private val macroGoalRepository: MacroGoalRepository,
    private val historyRepository: HistoryRepository,
    coroutineScope: CoroutineScope? = null
) : BaseViewModel<DashboardUiState, HistoryEffect>(DashboardUiState()) {

    private val scope = coroutineScope ?: viewModelScope

    private val dateFlow: Flow<LocalDate> = flow {
        while (true) {
            emit(LocalDate.now())
            // Check every minute if the date has changed
            delay(1.minutes)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val todayMealsFlow = dateFlow.flatMapLatest { date ->
        val start = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1)
        mealRepository.getMealsBetween(start, end)
    }

    init {
        scope.launch {
            combine(
                todayMealsFlow,
                macroGoalRepository.getMacroGoal(),
                historyRepository.getAllHistory()
            ) { meals, goal, history ->
                DashboardUiState(
                    meals = meals,
                    goal = goal,
                    history = history,
                    isLoading = false
                )
            }.collect { state ->
                updateState { state }
            }
        }
    }

    fun startNewDay() {
        scope.launch {
            val currentState = uiState.value
            val meals = currentState.meals
            if (meals.isEmpty()) return@launch

            // Use the date of the first meal as the "day it started"
            val startedDate = meals.minByOrNull { it.timestamp }
                ?.timestamp?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: LocalDate.now()

            val totalCalories = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.calories } }
            val totalProtein = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.protein.toDouble() } }.toFloat()
            val totalCarbs = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.carbs.toDouble() } }.toFloat()
            val totalFat = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.fat.toDouble() } }.toFloat()

            val historyEntry = HistoryEntry(
                date = startedDate,
                totalCalories = totalCalories,
                totalProtein = totalProtein,
                totalCarbs = totalCarbs,
                totalFat = totalFat,
                goalCalories = currentState.goal?.calories ?: 0,
                goalProtein = currentState.goal?.protein ?: 0f,
                goalCarbs = currentState.goal?.carbs ?: 0f,
                goalFat = currentState.goal?.fat ?: 0f
            )

            historyRepository.insertHistoryEntry(historyEntry)

            // Clear the list by deleting the meals
            meals.forEach { meal ->
                mealRepository.deleteMeal(meal)
            }

            sendEffect(HistoryEffect.DayStarted)
        }
    }

    fun deleteMeal(meal: Meal) {
        scope.launch {
            try {
                mealRepository.deleteMeal(meal)
            } catch (e: Exception) {
                sendEffect(HistoryEffect.ShowError(e.message ?: "Failed to delete meal"))
            }
        }
    }

    fun deleteHistoryEntry(entry: HistoryEntry) {
        scope.launch {
            try {
                historyRepository.deleteHistoryEntry(entry)
            } catch (e: Exception) {
                sendEffect(HistoryEffect.ShowError(e.message ?: "Failed to delete history entry"))
            }
        }
    }
}
