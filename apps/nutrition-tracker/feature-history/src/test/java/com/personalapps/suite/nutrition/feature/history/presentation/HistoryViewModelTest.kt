package com.personalapps.suite.nutrition.feature.history.presentation

import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.testing.MainDispatcherRule
import com.personalapps.suite.nutrition.feature.api.repository.HistoryRepository
import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    override suspend fun insertMeal(meal: Meal): Long {
        val current = meals.value.toMutableList()
        val index = current.indexOfFirst { it.id == meal.id && meal.id != 0L }
        if (index != -1) {
            current[index] = meal
        } else {
            current.add(meal.copy(id = if (meal.id == 0L) (current.size + 1).toLong() else meal.id))
        }
        meals.value = current
        return meal.id
    }
    override suspend fun deleteMeal(meal: Meal) {
        val current = meals.value.toMutableList()
        current.removeIf { it.id == meal.id }
        meals.value = current
    }
}

class FakeMacroGoalRepository : MacroGoalRepository {
    private val goal = MutableStateFlow<MacroGoal?>(null)
    override fun getMacroGoal(): Flow<MacroGoal?> = goal
    override suspend fun insertMacroGoal(macroGoal: MacroGoal): Long {
        goal.value = macroGoal
        return 1L
    }
}

class FakeHistoryRepository : HistoryRepository {
    private val history = MutableStateFlow<List<HistoryEntry>>(emptyList())
    override fun getAllHistory(): Flow<List<HistoryEntry>> = history
    override suspend fun insertHistoryEntry(entry: HistoryEntry) {
        history.value += entry
    }
    override suspend fun deleteHistoryEntry(entry: HistoryEntry) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mealRepository = FakeMealRepository()
    private val macroGoalRepository = FakeMacroGoalRepository()
    private val historyRepository = FakeHistoryRepository()
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        viewModel = HistoryViewModel(mealRepository, macroGoalRepository, historyRepository, null)
    }

    @Test
    fun init_combinesTodayMealsAndMacroGoal() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }
        runCurrent()

        macroGoalRepository.insertMacroGoal(MacroGoal(id = 1, calories = 2000, protein = 120f, carbs = 200f, fat = 70f))
        runCurrent()

        assertEquals(2000, viewModel.uiState.value.goal?.calories)
    }

    @Test
    fun updateMealPortion_updatesMacrosAndRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }
        
        val portion = LoggedFoodPortion(
            name = "Apple",
            calories = 52,
            protein = 0.3f,
            carbs = 13.8f,
            fat = 0.2f,
            amountGrams = 100f
        )
        val meal = Meal(
            id = 1,
            name = "Snack",
            timestamp = Instant.now(),
            loggedFoods = listOf(portion)
        )
        
        mealRepository.insertMeal(meal)
        runCurrent()
        
        viewModel.updateMealPortion(meal, portion, 200f)
        runCurrent()
        
        val updatedMeal = mealRepository.getAllMeals().first().first()
        val updatedPortion = updatedMeal.loggedFoods.first()
        
        assertEquals(200f, updatedPortion.amountGrams)
        assertEquals(104, updatedPortion.calories)
        assertEquals(0.6f, updatedPortion.protein, 0.01f)
        assertEquals(27.6f, updatedPortion.carbs, 0.01f)
        assertEquals(0.4f, updatedPortion.fat, 0.01f)
    }

    @Test
    fun startNewDay_groupsMealsByDateAndClearsList() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        // Use fixed timestamps to avoid timezone issues in tests if possible, 
        // though ZoneId.systemDefault() will be used by the ViewModel.
        val date1 = Instant.parse("2023-01-01T12:00:00Z")
        val date2 = Instant.parse("2023-01-02T12:00:00Z")

        val meal1 = Meal(
            id = 1,
            name = "Meal 1",
            timestamp = date1,
            loggedFoods = listOf(LoggedFoodPortion("Apple", 52, 0.3f, 13.8f, 0.2f, 100f))
        )
        val meal2 = Meal(
            id = 2,
            name = "Meal 2",
            timestamp = date2,
            loggedFoods = listOf(LoggedFoodPortion("Banana", 89, 1.1f, 22.8f, 0.3f, 100f))
        )

        mealRepository.insertMeal(meal1)
        mealRepository.insertMeal(meal2)
        runCurrent()

        viewModel.startNewDay()
        runCurrent()

        val history = historyRepository.getAllHistory().first()
        assertEquals(2, history.size)

        // Grouping by date in ViewModel uses system default zone
        val localDate1 = date1.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        val localDate2 = date2.atZone(java.time.ZoneId.systemDefault()).toLocalDate()

        val entry1 = history.find { it.date == localDate1 }
        val entry2 = history.find { it.date == localDate2 }

        assertEquals(52, entry1?.totalCalories)
        assertEquals(89, entry2?.totalCalories)

        val remainingMeals = mealRepository.getAllMeals().first()
        assertEquals(0, remainingMeals.size)
    }
}

