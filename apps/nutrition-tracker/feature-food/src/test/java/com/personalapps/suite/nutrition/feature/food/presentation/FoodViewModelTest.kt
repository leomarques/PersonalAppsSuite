package com.personalapps.suite.nutrition.feature.food.presentation

import com.personalapps.suite.nutrition.feature.food.domain.model.Food
import com.personalapps.suite.nutrition.feature.food.domain.repository.FoodRepository
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
    fun addFood_insertsIntoRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.foodsState.collect {}
        }

        viewModel.addFood("Banana", 89, 1.1f, 22.8f, 0.3f)
        runCurrent()

        val foods = viewModel.foodsState.value
        assertEquals(1, foods.size)
        assertEquals("Banana", foods.first().name)
        assertEquals(89, foods.first().calories)
    }

    @Test
    fun deleteFood_removesFromRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.foodsState.collect {}
        }

        viewModel.addFood("Banana", 89, 1.1f, 22.8f, 0.3f)
        runCurrent()
        
        val addedFood = repository.getAllFoods().first().first()

        viewModel.deleteFood(addedFood)
        runCurrent()

        val foods = viewModel.foodsState.value
        assertEquals(0, foods.size)
    }
}
