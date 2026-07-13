package com.personalapps.suite.nutrition.feature.history.presentation

import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.testing.MainDispatcherRule
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FakeMealRepository : MealRepository {
    private val meals = MutableStateFlow<List<Meal>>(emptyList())
    override fun getAllMeals(): Flow<List<Meal>> = meals
    override fun getMealsBetween(startDate: Instant, endDate: Instant): Flow<List<Meal>> = meals
    override suspend fun insertMeal(meal: Meal): Long = 0L
    override suspend fun deleteMeal(meal: Meal) {}
}

class FakeMacroGoalRepository : MacroGoalRepository {
    private val goal = MutableStateFlow<MacroGoal?>(null)
    override fun getMacroGoal(): Flow<MacroGoal?> = goal
    override suspend fun insertMacroGoal(macroGoal: MacroGoal): Long {
        goal.value = macroGoal
        return 1L
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mealRepository = FakeMealRepository()
    private val macroGoalRepository = FakeMacroGoalRepository()
    private lateinit var viewModel: HistoryViewModel

    @Test
    fun init_combinesTodayMealsAndMacroGoal() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel = HistoryViewModel(mealRepository, macroGoalRepository, backgroundScope)
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }
        runCurrent()

        macroGoalRepository.insertMacroGoal(MacroGoal(id = 1, calories = 2000, protein = 120f, carbs = 200f, fat = 70f))
        runCurrent()

        assertEquals(2000, viewModel.uiState.value.goal?.calories)
    }
}
