package com.personalapps.suite.nutrition.feature.meals.presentation

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.nutrition.feature.meals.domain.usecase.LogMealUseCase
import com.personalapps.suite.shared.common.DateProvider
import com.personalapps.suite.shared.common.Result
import com.personalapps.suite.shared.databaseutils.TransactionProvider
import com.personalapps.suite.shared.testing.MainDispatcherRule
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
import java.time.Instant
import java.time.LocalDate

class FakeFoodRepository : FoodRepository {
    private val _foods = MutableStateFlow<List<Food>>(emptyList())

    override fun getAllFoods(): Flow<List<Food>> = _foods

    override suspend fun insertFood(food: Food): Result<Long> {
        val list = _foods.value.toMutableList()
        val newFood = food.copy(id = (list.size + 1).toLong())
        list.add(newFood)
        sortFoods(list)
        return Result.Success(newFood.id)
    }

    override suspend fun updateFood(food: Food): Result<Unit> {
        val list = _foods.value.toMutableList()
        val index = list.indexOfFirst { it.id == food.id }
        if (index != -1) {
            list[index] = food
            sortFoods(list)
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteFood(food: Food): Result<Unit> {
        val list = _foods.value.toMutableList()
        list.removeIf { it.id == food.id }
        sortFoods(list)
        return Result.Success(Unit)
    }

    override suspend fun incrementFrequency(foodId: Long): Result<Unit> {
        val list = _foods.value.toMutableList()
        val index = list.indexOfFirst { it.id == foodId }
        if (index != -1) {
            list[index] = list[index].copy(frequency = list[index].frequency + 1)
            sortFoods(list)
        }
        return Result.Success(Unit)
    }

    override suspend fun incrementFrequencyByName(name: String): Result<Unit> {
        val list = _foods.value.toMutableList()
        val index = list.indexOfFirst { it.name == name }
        if (index != -1) {
            list[index] = list[index].copy(frequency = list[index].frequency + 1)
            sortFoods(list)
        }
        return Result.Success(Unit)
    }

    private fun sortFoods(list: MutableList<Food>) {
        list.sortWith(
            compareByDescending<Food> { it.frequency }
                .thenBy { it.name }
        )
        _foods.value = list
    }
}

class FakeMealRepository : MealRepository {
    private val meals = MutableStateFlow<List<Meal>>(emptyList())

    override fun getAllMeals(): Flow<List<Meal>> = meals

    override suspend fun insertMeal(meal: Meal): Result<Long> {
        val list = meals.value.toMutableList()
        val newMeal = meal.copy(id = (list.size + 1).toLong())
        list.add(newMeal)
        meals.value = list
        return Result.Success(newMeal.id)
    }

    override suspend fun deleteMeal(meal: Meal): Result<Unit> {
        val list = meals.value.toMutableList()
        list.remove(meal)
        meals.value = list
        return Result.Success(Unit)
    }
}

class FakeDateProvider : DateProvider {
    override fun now(): LocalDate = LocalDate.EPOCH
    override fun nowInstant(): Instant = Instant.now()
}

class FakeTransactionProvider : TransactionProvider {
    override suspend fun <T> runInTransaction(block: suspend () -> T): T = block()
}

@OptIn(ExperimentalCoroutinesApi::class)
class MealViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val foodRepository = FakeFoodRepository()
    private val mealRepository = FakeMealRepository()
    private val dateProvider = FakeDateProvider()
    private val transactionProvider = FakeTransactionProvider()
    private lateinit var logMealUseCase: LogMealUseCase
    private lateinit var viewModel: MealViewModel

    @Before
    fun setUp() {
        logMealUseCase = LogMealUseCase(mealRepository, foodRepository, dateProvider, transactionProvider)
        viewModel = MealViewModel(mealRepository, foodRepository, logMealUseCase)
    }

    @Test
    fun addCustomFood_insertsIntoFoodRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        viewModel.addCustomFood("Apple", 52, 0.3f, 13.8f, 0.2f, 100f)
        runCurrent()

        val foods = foodRepository.getAllFoods().first()
        assertEquals(1, foods.size)
        assertEquals("Apple", foods.first().name)
        assertEquals(52, foods.first().calories)
        assertEquals(100f, foods.first().gramsPerServing)
    }

    @Test
    fun addCustomFood_withSpecificServingSize_insertsIntoFoodRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        viewModel.addCustomFood("Protein Bar", 200, 20f, 15f, 5f, 60f)
        runCurrent()

        val foods = foodRepository.getAllFoods().first()
        assertEquals(1, foods.size)
        assertEquals("Protein Bar", foods.first().name)
        assertEquals(60f, foods.first().gramsPerServing)
    }

    @Test
    fun logSingleFoodPortion_insertsMealIntoRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        val apple = Food(id = 1, name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f, gramsPerServing = 100f)

        viewModel.logSingleFoodPortion(apple, 150f)
        runCurrent()

        val loggedMeals = mealRepository.getAllMeals().first()
        assertEquals(1, loggedMeals.size)
        assertEquals("Apple", loggedMeals.first().name)

        val portions = loggedMeals.first().loggedFoods
        assertEquals(1, portions.size)
        assertEquals("Apple", portions.first().name)
        assertEquals(150f, portions.first().amountGrams)
        // 52 kcal * (150/100) = 78 kcal
        assertEquals(78, portions.first().calories)
    }

    @Test
    fun logSingleFoodPortion_withCustomServingSize_calculatesCorrectNutrients() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        val proteinBar = Food(id = 2, name = "Protein Bar", calories = 200, protein = 20f, carbs = 15f, fat = 5f, gramsPerServing = 50f)

        // Log 100g (2 servings)
        viewModel.logSingleFoodPortion(proteinBar, 100f)
        runCurrent()

        val loggedMeals = mealRepository.getAllMeals().first()
        val portions = loggedMeals.first().loggedFoods
        assertEquals(1, portions.size)
        // 200 kcal * (100/50) = 400 kcal
        assertEquals(400, portions.first().calories)
        // 20g protein * 2 = 40g
        assertEquals(40f, portions.first().protein)
    }

    @Test
    fun foods_areSortedByFrequencyThenName() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        // Add some foods
        foodRepository.insertFood(Food(name = "Zucchini", calories = 17, protein = 1.2f, carbs = 3.1f, fat = 0.3f))
        foodRepository.insertFood(Food(name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f))
        foodRepository.insertFood(Food(name = "Banana", calories = 89, protein = 1.1f, carbs = 22.8f, fat = 0.3f))
        runCurrent()

        // Initially sorted by name: Apple, Banana, Zucchini
        assertEquals(listOf("Apple", "Banana", "Zucchini"), viewModel.uiState.value.foods.map { it.name })

        // Log Banana twice and Apple once
        val banana = Food(name = "Banana", calories = 89, protein = 1.1f, carbs = 22.8f, fat = 0.3f)
        val apple = Food(name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f)

        viewModel.logSingleFoodPortion(banana, 100f)
        viewModel.logSingleFoodPortion(banana, 120f)
        viewModel.logSingleFoodPortion(apple, 150f)
        runCurrent()

        // Now sorted by frequency: Banana (2), Apple (1), Zucchini (0)
        assertEquals(listOf("Banana", "Apple", "Zucchini"), viewModel.uiState.value.foods.map { it.name })
    }
}
