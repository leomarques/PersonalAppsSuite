package com.personalapps.suite.nutrition.feature.food.presentation

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
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

class FakeFoodRepository : FoodRepository {
    private val _foods = MutableStateFlow<List<Food>>(emptyList())

    override fun getAllFoods(): Flow<List<Food>> = _foods

    override suspend fun insertFood(food: Food): Long {
        val updated = _foods.value.toMutableList()
        val newFood = food.copy(id = (updated.size + 1).toLong())
        updated.add(newFood)
        sortFoods(updated)
        return newFood.id
    }

    override suspend fun deleteFood(food: Food) {
        val updated = _foods.value.toMutableList()
        updated.removeIf { it.id == food.id }
        sortFoods(updated)
    }

    override suspend fun updateFood(food: Food) {
        val updated = _foods.value.toMutableList()
        updated.removeIf { it.id == food.id }
        updated.add(food)
        sortFoods(updated)
    }

    override suspend fun incrementFrequency(foodId: Long) {
        val updated = _foods.value.toMutableList()
        val index = updated.indexOfFirst { it.id == foodId }
        if (index != -1) {
            val food = updated[index]
            updated[index] = food.copy(frequency = food.frequency + 1)
            sortFoods(updated)
        }
    }

    override suspend fun incrementFrequencyByName(name: String) {
        val updated = _foods.value.toMutableList()
        val index = updated.indexOfFirst { it.name == name }
        if (index != -1) {
            val food = updated[index]
            updated[index] = food.copy(frequency = food.frequency + 1)
            sortFoods(updated)
        }
    }

    private fun sortFoods(list: MutableList<Food>) {
        list.sortWith(
            compareByDescending<Food> { it.frequency }
                .thenBy { it.name }
        )
        _foods.value = list
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FoodViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeFoodRepository()
    private lateinit var viewModel: FoodViewModel

    @Before
    fun setUp() {
        viewModel = FoodViewModel(repository)
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

        // Initially sorted by name: Apple, Banana, Zucchini (frequency 0)
        assertEquals(listOf("Apple", "Banana", "Zucchini"), viewModel.uiState.value.foods.map { it.name })

        // Increment frequency
        val bananaId = viewModel.uiState.value.foods.first { it.name == "Banana" }.id
        val appleId = viewModel.uiState.value.foods.first { it.name == "Apple" }.id

        repository.incrementFrequency(bananaId)
        repository.incrementFrequency(bananaId)
        repository.incrementFrequency(appleId)
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
