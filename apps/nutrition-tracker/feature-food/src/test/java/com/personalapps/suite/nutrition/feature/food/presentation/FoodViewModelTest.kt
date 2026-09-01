package com.personalapps.suite.nutrition.feature.food.presentation

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.food.domain.usecase.AddFoodUseCase
import com.personalapps.suite.nutrition.feature.food.domain.usecase.DeleteFoodUseCase
import com.personalapps.suite.nutrition.feature.food.domain.usecase.UpdateFoodUseCase
import com.personalapps.suite.shared.common.Result
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

    override suspend fun insertFood(food: Food): Result<Long> {
        val updated = _foods.value.toMutableList()
        val newFood = food.copy(id = (updated.size + 1).toLong())
        updated.add(newFood)
        sortFoods(updated)
        _foods.value = updated
        return Result.Success(newFood.id)
    }

    override suspend fun deleteFood(food: Food): Result<Unit> {
        val updated = _foods.value.toMutableList()
        updated.removeIf { it.id == food.id }
        _foods.value = updated
        return Result.Success(Unit)
    }

    override suspend fun updateFood(food: Food): Result<Unit> {
        val updated = _foods.value.toMutableList()
        updated.removeIf { it.id == food.id }
        updated.add(food)
        sortFoods(updated)
        _foods.value = updated
        return Result.Success(Unit)
    }

    override suspend fun updateLastUsed(foodId: Long): Result<Unit> {
        val updated = _foods.value.toMutableList()
        val index = updated.indexOfFirst { it.id == foodId }
        if (index != -1) {
            val food = updated[index]
            updated[index] = food.copy(
                lastUsedAt = System.currentTimeMillis()
            )
            sortFoods(updated)
            _foods.value = updated
        }
        return Result.Success(Unit)
    }

    override suspend fun updateLastUsedByName(name: String): Result<Unit> {
        val updated = _foods.value.toMutableList()
        val index = updated.indexOfFirst { it.name == name }
        if (index != -1) {
            val food = updated[index]
            updated[index] = food.copy(
                lastUsedAt = System.currentTimeMillis()
            )
            sortFoods(updated)
            _foods.value = updated
        }
        return Result.Success(Unit)
    }

    private fun sortFoods(list: MutableList<Food>) {
        list.sortWith(
            compareByDescending<Food> { it.lastUsedAt }
                .thenBy { it.name }
        )
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
        val addFoodUseCase = AddFoodUseCase(repository)
        val updateFoodUseCase = UpdateFoodUseCase(repository)
        val deleteFoodUseCase = DeleteFoodUseCase(repository)
        viewModel = FoodViewModel(repository, addFoodUseCase, updateFoodUseCase, deleteFoodUseCase)
    }

    @Test
    fun foods_areSortedByRecencyThenName() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        // Add some foods
        repository.insertFood(Food(name = "Zucchini", calories = 17, protein = 1.2f, carbs = 3.1f, fat = 0.3f))
        repository.insertFood(Food(name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f))
        repository.insertFood(Food(name = "Banana", calories = 89, protein = 1.1f, carbs = 22.8f, fat = 0.3f))
        runCurrent()

        // Initially sorted by name since lastUsedAt is 0 for all
        assertEquals(listOf("Apple", "Banana", "Zucchini"), viewModel.uiState.value.foods.map { it.name })

        // Update last used (which updates lastUsedAt)
        val bananaId = viewModel.uiState.value.foods.first { it.name == "Banana" }.id
        repository.updateLastUsed(bananaId)
        runCurrent()

        // Now Banana should be at the top
        assertEquals(listOf("Banana", "Apple", "Zucchini"), viewModel.uiState.value.foods.map { it.name })
        
        val appleId = viewModel.uiState.value.foods.first { it.name == "Apple" }.id
        repository.updateLastUsed(appleId)
        runCurrent()
        
        // Now Apple should be at the top
        assertEquals(listOf("Apple", "Banana", "Zucchini"), viewModel.uiState.value.foods.map { it.name })
    }

    @Test
    fun addFood_insertsIntoRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        viewModel.addFood("Banana", "89", "1.1", "22.8", "0.3")
        runCurrent()

        val foods = viewModel.uiState.value.foods
        assertEquals(1, foods.size)
        assertEquals("Banana", foods.first().name)
        assertEquals(89, foods.first().calories)
    }

    @Test
    fun updateFood_preservesMetadata() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        repository.insertFood(Food(name = "Banana", calories = 89, protein = 1.1f, carbs = 22.8f, fat = 0.3f, lastUsedAt = 12345L))
        runCurrent()
        
        val foodBefore = viewModel.uiState.value.foods.first()
        assertEquals(12345L, foodBefore.lastUsedAt)

        viewModel.updateFood(foodBefore.copy(name = "Updated Banana", calories = 100))
        runCurrent()

        val foodAfter = viewModel.uiState.value.foods.first()
        assertEquals("Updated Banana", foodAfter.name)
        assertEquals(100, foodAfter.calories)
        assertEquals(12345L, foodAfter.lastUsedAt)
    }

    @Test
    fun deleteFood_removesFromRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        viewModel.addFood("Banana", "89", "1.1", "22.8", "0.3")
        runCurrent()
        
        val addedFood = repository.getAllFoods().first().first()

        viewModel.deleteFood(addedFood)
        runCurrent()

        val foods = viewModel.uiState.value.foods
        assertEquals(0, foods.size)
    }
}
