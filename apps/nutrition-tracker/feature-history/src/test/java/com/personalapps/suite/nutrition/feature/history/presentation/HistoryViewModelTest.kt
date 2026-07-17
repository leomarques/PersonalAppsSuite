package com.personalapps.suite.nutrition.feature.history.presentation

import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.nutrition.feature.api.repository.HistoryRepository
import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import com.personalapps.suite.nutrition.feature.history.domain.usecase.StartNewDayUseCase
import com.personalapps.suite.shared.common.DateProvider
import com.personalapps.suite.shared.common.Result
import com.personalapps.suite.shared.preferences.PreferencesManager
import com.personalapps.suite.shared.testing.MainDispatcherRule
import java.time.LocalDate
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
    override suspend fun insertMeal(meal: Meal): Result<Long> {
        val current = meals.value.toMutableList()
        val index = current.indexOfFirst { it.id == meal.id && meal.id != 0L }
        val id = if (index != -1) {
            current[index] = meal
            meal.id
        } else {
            val newId = (current.size + 1).toLong()
            current.add(meal.copy(id = if (meal.id == 0L) newId else meal.id))
            if (meal.id == 0L) newId else meal.id
        }
        meals.value = current
        return Result.Success(id)
    }
    override suspend fun deleteMeal(meal: Meal): Result<Unit> {
        val current = meals.value.toMutableList()
        current.removeIf { it.id == meal.id }
        meals.value = current
        return Result.Success(Unit)
    }
}

class FakeMacroGoalRepository : MacroGoalRepository {
    private val goal = MutableStateFlow<MacroGoal?>(null)
    override fun getMacroGoal(): Flow<MacroGoal?> = goal
    override suspend fun insertMacroGoal(goal: MacroGoal): Result<Long> {
        this.goal.value = goal
        return Result.Success(1L)
    }
}

class FakeHistoryRepository : HistoryRepository {
    private val history = MutableStateFlow<List<HistoryEntry>>(emptyList())
    override fun getAllHistory(): Flow<List<HistoryEntry>> = history
    override suspend fun getHistoryEntryByDate(date: LocalDate): Result<HistoryEntry?> {
        return Result.Success(history.value.find { it.date == date })
    }
    override suspend fun insertHistoryEntry(entry: HistoryEntry): Result<Unit> {
        val current = history.value.toMutableList()
        val index = current.indexOfFirst { it.date == entry.date }
        if (index != -1) {
            current[index] = entry
        } else {
            current.add(entry)
        }
        history.value = current
        return Result.Success(Unit)
    }
    override suspend fun deleteHistoryEntry(entry: HistoryEntry): Result<Unit> = Result.Success(Unit)
}

class FakePreferencesManager : PreferencesManager {
    private val openDayDate = MutableStateFlow<String?>(null)
    override fun getOpenDayDate(): Flow<String?> = openDayDate
    override suspend fun setOpenDayDate(date: String) {
        openDayDate.value = date
    }
}

class FakeDateProvider(private var fixedDate: LocalDate) : DateProvider {
    fun setDate(date: LocalDate) { fixedDate = date }
    override fun now(): LocalDate = fixedDate
}

class FakeTransactionProvider : com.personalapps.suite.shared.databaseutils.TransactionProvider {
    override suspend fun <T> runInTransaction(block: suspend () -> T): T = block()
}

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mealRepository = FakeMealRepository()
    private val macroGoalRepository = FakeMacroGoalRepository()
    private val historyRepository = FakeHistoryRepository()
    private val preferencesManager = FakePreferencesManager()
    private val dateProvider = FakeDateProvider(LocalDate.now())
    private val transactionProvider = FakeTransactionProvider()
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        val startNewDayUseCase = StartNewDayUseCase(
            mealRepository,
            macroGoalRepository,
            historyRepository,
            preferencesManager,
            dateProvider,
            transactionProvider
        )
        viewModel = HistoryViewModel(
            mealRepository,
            macroGoalRepository,
            historyRepository,
            startNewDayUseCase,
            null
        )
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
    fun startNewDay_usesStoredDateAndClearsList() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        val storedDate = LocalDate.of(2023, 1, 1)
        val today = LocalDate.of(2023, 1, 2)
        preferencesManager.setOpenDayDate(storedDate.toString())
        dateProvider.setDate(today)

        val meal1 = Meal(
            id = 1,
            name = "Meal 1",
            loggedFoods = listOf(LoggedFoodPortion("Apple", 52, 0.3f, 13.8f, 0.2f, 100f))
        )
        val meal2 = Meal(
            id = 2,
            name = "Meal 2",
            loggedFoods = listOf(LoggedFoodPortion("Banana", 89, 1.1f, 22.8f, 0.3f, 100f))
        )

        mealRepository.insertMeal(meal1)
        mealRepository.insertMeal(meal2)
        runCurrent()

        viewModel.startNewDay()
        runCurrent()

        val history = historyRepository.getAllHistory().first()
        assertEquals(1, history.size)

        val entry = history.first()
        assertEquals(storedDate, entry.date)
        assertEquals(52 + 89, entry.totalCalories)

        val remainingMeals = mealRepository.getAllMeals().first()
        assertEquals(0, remainingMeals.size)
        
        assertEquals(today.toString(), preferencesManager.getOpenDayDate().first())
    }
}

