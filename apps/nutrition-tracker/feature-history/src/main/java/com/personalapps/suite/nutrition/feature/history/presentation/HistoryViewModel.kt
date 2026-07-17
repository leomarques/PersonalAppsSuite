package com.personalapps.suite.nutrition.feature.history.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.repository.HistoryRepository
import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.nutrition.feature.history.domain.usecase.StartNewDayUseCase
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class DashboardUiState(
    val meals: List<Meal> = emptyList(),
    val goal: MacroGoal? = null,
    val history: List<HistoryEntry> = emptyList(),
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
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
    private val startNewDayUseCase: StartNewDayUseCase,
    coroutineScope: CoroutineScope? = null
) : BaseViewModel<DashboardUiState, HistoryEffect>(DashboardUiState()) {

    private val scope = coroutineScope ?: viewModelScope

    init {
        scope.launch {
            combine(
                mealRepository.getAllMeals(),
                macroGoalRepository.getMacroGoal(),
                historyRepository.getAllHistory()
            ) { meals, goal, history ->
                val totalCalories = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.calories } }
                val totalProtein = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.protein.toDouble() } }.toFloat()
                val totalCarbs = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.carbs.toDouble() } }.toFloat()
                val totalFat = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.fat.toDouble() } }.toFloat()

                DashboardUiState(
                    meals = meals,
                    goal = goal,
                    history = history,
                    totalCalories = totalCalories,
                    totalProtein = totalProtein,
                    totalCarbs = totalCarbs,
                    totalFat = totalFat,
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
            val result = startNewDayUseCase(currentState.meals, currentState.goal)
            handleResult(
                result = result,
                onSuccess = { sendEffect(HistoryEffect.DayStarted) },
                onError = { sendEffect(HistoryEffect.ShowError(it.message ?: "Failed to start new day")) }
            )
        }
    }

    fun deleteMeal(meal: Meal) {
        scope.launch {
            val result = mealRepository.deleteMeal(meal)
            handleResult(
                result = result,
                onSuccess = { /* Success state already handled by flow collection */ },
                onError = { sendEffect(HistoryEffect.ShowError(it.message ?: "Failed to delete meal")) }
            )
        }
    }

    fun updateMealPortion(meal: Meal, portion: LoggedFoodPortion, newAmountGrams: Float) {
        scope.launch {
            try {
                // To maintain nutrients per 100g accuracy, we calculate factor based on amountGrams
                val factor = newAmountGrams / portion.amountGrams
                val updatedPortion = portion.copy(
                    calories = (portion.calories * factor).toInt(),
                    protein = portion.protein * factor,
                    carbs = portion.carbs * factor,
                    fat = portion.fat * factor,
                    amountGrams = newAmountGrams,
                    gramsPerServing = portion.gramsPerServing
                )
                
                val updatedLoggedFoods = meal.loggedFoods.map {
                    if (it == portion) updatedPortion else it
                }
                
                val updatedMeal = meal.copy(loggedFoods = updatedLoggedFoods)
                val result = mealRepository.insertMeal(updatedMeal)
                handleResult(
                    result = result,
                    onSuccess = { /* Success state already handled by flow collection */ },
                    onError = { sendEffect(HistoryEffect.ShowError(it.message ?: "Failed to update meal")) }
                )
            } catch (e: Exception) {
                sendEffect(HistoryEffect.ShowError(e.message ?: "Failed to update meal"))
            }
        }
    }

    fun deleteHistoryEntry(entry: HistoryEntry) {
        scope.launch {
            val result = historyRepository.deleteHistoryEntry(entry)
            handleResult(
                result = result,
                onSuccess = { /* Success state already handled by flow collection */ },
                onError = { sendEffect(HistoryEffect.ShowError(it.message ?: "Failed to delete history entry")) }
            )
        }
    }
}
