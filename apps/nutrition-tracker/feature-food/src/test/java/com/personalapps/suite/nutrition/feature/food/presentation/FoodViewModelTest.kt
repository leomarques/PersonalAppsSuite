package com.personalapps.suite.nutrition.feature.food.presentation

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
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

class FakeFoodRepository : FoodRepository {
    private val foods = MutableStateFlow<List<Food>>(emptyList())

    override fun getAllFoods(): Flow<List<Food>> = foods

    override suspend fun insertFood(food: Food): Long {
        val updated = foods.value.toMutableList()
        val newFood = food.copy(id = (updated.size + 1).toLong())
        updated.add(newFood)
        foods.value = updated
        return newFood.id
    }

    override suspend fun deleteFood(food: Food) {
        val updated = foods.value.toMutableList()
        updated.remove(food)
        foods.value = updated
    }
}

class FakeMealRepository : MealRepository {
    private val meals = MutableStateFlow<List<Meal>>(emptyList())

    override fun getAllMeals(): Flow<List<Meal>> = meals

    override fun getMealsBetween(startDate: Instant, endDate: Instant): Flow<List<Meal>> = meals

    override suspend fun insertMeal(meal: Meal): Long {
        val list = meals.value.toMutableList()
        val newMeal = meal.copy(id = (list.size + 1).toLong())
        list.add(newMeal)
        meals.value = list
        return newMeal.id
    }

    override suspend fun deleteMeal(meal: Meal) {
        val list = meals.value.toMutableList()
        list.remove(meal)
        meals.value = list
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FoodViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeFoodRepository()
    private val mealRepository = FakeMealRepository()
    private lateinit var viewModel: FoodViewModel

    @Before
    fun setUp() {
        viewModel = FoodViewModel(repository, mealRepository)
    }

    @Test
    fun foods_areSortedByFrequencyThenName() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        // Add some foods
        repository.insertFood(Food(name = "Zucchini", calories = 17, protein = 1.2f, carbs = 3.1f, fat = 0.3f))
        repository.insertFood(Food(name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f))
        repository.insertFood(Food(name = "Banana", calories = 89, protein = 1.1f, carbs = 22.8f, fat = 0.3f))
        runCurrent()

        // Initially sorted by name: Apple, Banana, Zucchini
        assertEquals(listOf("Apple", "Banana", "Zucchini"), viewModel.uiState.value.foods.map { it.name })

        // Log Banana twice and Apple once
        val bananaPortion = com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion(
            name = "Banana", calories = 89, protein = 1.1f, carbs = 22.8f, fat = 0.3f, amountGrams = 100f
        )
        val applePortion = com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion(
            name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f, amountGrams = 100f
        )

        mealRepository.insertMeal(Meal(name = "Lunch", timestamp = Instant.now(), loggedFoods = listOf(bananaPortion)))
        mealRepository.insertMeal(Meal(name = "Snack", timestamp = Instant.now(), loggedFoods = listOf(bananaPortion)))
        mealRepository.insertMeal(Meal(name = "Breakfast", timestamp = Instant.now(), loggedFoods = listOf(applePortion)))
        runCurrent()

        // Now sorted by frequency: Banana (2), Apple (1), Zucchini (0)
        assertEquals(listOf("Banana", "Apple", "Zucchini"), viewModel.uiState.value.foods.map { it.name })
    }

    @Test
    fun addFood_insertsIntoRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        viewModel.addFood("Banana", 89, 1.1f, 22.8f, 0.3f)
        runCurrent()

        val foods = viewModel.uiState.value.foods
        assertEquals(1, foods.size)
        assertEquals("Banana", foods.first().name)
        assertEquals(89, foods.first().calories)
    }

    @Test
    fun deleteFood_removesFromRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        viewModel.addFood("Banana", 89, 1.1f, 22.8f, 0.3f)
        runCurrent()
        
        val addedFood = repository.getAllFoods().first().first()

        viewModel.deleteFood(addedFood)
        runCurrent()

        val foods = viewModel.uiState.value.foods
        assertEquals(0, foods.size)
    }
}
