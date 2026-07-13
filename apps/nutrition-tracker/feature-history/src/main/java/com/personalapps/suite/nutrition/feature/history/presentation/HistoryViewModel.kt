package com.personalapps.suite.nutrition.feature.history.presentation

import androidx.lifecycle.viewModelScope
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
    val isLoading: Boolean = true
)

sealed interface HistoryEffect {
    data class ShowError(val message: String) : HistoryEffect
}

class HistoryViewModel(
    private val mealRepository: MealRepository,
    macroGoalRepository: MacroGoalRepository,
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
            combine(todayMealsFlow, macroGoalRepository.getMacroGoal()) { meals, goal ->
                DashboardUiState(meals = meals, goal = goal, isLoading = false)
            }.collect { state ->
                updateState { state }
            }
        }
    }
}
