package com.personalapps.suite.nutrition.feature.meals.presentation

import com.personalapps.suite.nutrition.feature.food.domain.model.Food
import com.personalapps.suite.nutrition.feature.food.domain.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.meals.domain.model.Meal
import com.personalapps.suite.nutrition.feature.meals.domain.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.meals.domain.repository.MealRepository
import com.personalapps.suite.shared.testing.MainDispatcherRule
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

class FakeFoodRepository : FoodRepository {
    private val foods = MutableStateFlow<List<Food>>(emptyList())

    override fun getAllFoods(): Flow<List<Food>> = foods

    override suspend fun insertFood(food: Food): Long {
        val list = foods.value.toMutableList()
        val newFood = food.copy(id = (list.size + 1).toLong())
        list.add(newFood)
        foods.value = list
        return newFood.id
    }

    override suspend fun deleteFood(food: Food) {
        val list = foods.value.toMutableList()
        list.remove(food)
        foods.value = list
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
class MealViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val foodRepository = FakeFoodRepository()
    private val mealRepository = FakeMealRepository()
    private lateinit var viewModel: MealViewModel

    @Before
    fun setUp() {
        viewModel = MealViewModel(mealRepository, foodRepository)
    }

    @Test
    fun addCustomFood_insertsIntoFoodRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.foodsState.collect {}
        }

        viewModel.addCustomFood("Apple", 52, 0.3f, 13.8f, 0.2f)
        runCurrent()

        val foods = foodRepository.getAllFoods().first()
        assertEquals(1, foods.size)
        assertEquals("Apple", foods.first().name)
        assertEquals(52, foods.first().calories)
    }

    @Test
    fun logSingleFoodPortion_insertsMealIntoRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.mealsState.collect {}
        }

        val apple = Food(id = 1, name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f)

        viewModel.logSingleFoodPortion(apple, 150f, "Lunch")
        runCurrent()

        val loggedMeals = mealRepository.getAllMeals().first()
        assertEquals(1, loggedMeals.size)
        assertEquals("Lunch", loggedMeals.first().name)

        val portions = loggedMeals.first().loggedFoods
        assertEquals(1, portions.size)
        assertEquals("Apple", portions.first().name)
        assertEquals(150f, portions.first().amountGrams)
        // 52 kcal * 1.5 = 78 kcal
        assertEquals(78, portions.first().calories)
    }
}
