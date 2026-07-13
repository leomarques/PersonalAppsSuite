package com.personalapps.suite.nutrition.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.macros.domain.model.MacroGoal
import com.personalapps.suite.nutrition.feature.macros.domain.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.meals.domain.model.Meal
import com.personalapps.suite.nutrition.feature.meals.domain.repository.MealRepository
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    private val mealRepository: MealRepository,
    macroGoalRepository: MacroGoalRepository
) : ViewModel() {

    private val dateFlow: Flow<LocalDate> = flow {
        while (true) {
            emit(LocalDate.now())
            // Check every minute if the date has changed
            delay(1.minutes)
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todayMeals: StateFlow<List<Meal>> = dateFlow.flatMapLatest { date ->
        val start = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1)
        mealRepository.getMealsBetween(start, end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val macroGoal: StateFlow<MacroGoal?> = macroGoalRepository.getMacroGoal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dashboardState: StateFlow<DashboardUiState> = combine(todayMeals, macroGoal) { meals, goal ->
        DashboardUiState(meals = meals, goal = goal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}

data class DashboardUiState(
    val meals: List<Meal> = emptyList(),
    val goal: MacroGoal? = null
)
