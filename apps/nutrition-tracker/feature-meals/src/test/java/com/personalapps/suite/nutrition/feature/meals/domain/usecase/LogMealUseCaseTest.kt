package com.personalapps.suite.nutrition.feature.meals.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeMealRepository : MealRepository {
    private val meals = MutableStateFlow<List<Meal>>(emptyList())
    override fun getAllMeals(): Flow<List<Meal>> = meals
    override suspend fun insertMeal(meal: Meal): Long {
        val list = meals.value.toMutableList()
        val newMeal = meal.copy(id = (list.size + 1).toLong())
        list.add(newMeal)
        meals.value = list
        return newMeal.id
    }
    override suspend fun deleteMeal(meal: Meal) {}
}

class FakeFoodRepository : com.personalapps.suite.nutrition.feature.api.repository.FoodRepository {
    private val frequencies = mutableMapOf<Long, Int>()
    private val nameFrequencies = mutableMapOf<String, Int>()

    fun getFrequency(id: Long) = frequencies[id] ?: 0
    fun getFrequencyByName(name: String) = nameFrequencies[name] ?: 0

    override fun getAllFoods(): Flow<List<Food>> = MutableStateFlow(emptyList())
    override suspend fun insertFood(food: Food): Long = 0L
    override suspend fun updateFood(food: Food) {}
    override suspend fun deleteFood(food: Food) {}
    override suspend fun incrementFrequency(foodId: Long) {
        frequencies[foodId] = (frequencies[foodId] ?: 0) + 1
    }
    override suspend fun incrementFrequencyByName(name: String) {
        nameFrequencies[name] = (nameFrequencies[name] ?: 0) + 1
    }
}

class LogMealUseCaseTest {
    private val mealRepository = FakeMealRepository()
    private val foodRepository = FakeFoodRepository()
    private lateinit var useCase: LogMealUseCase

    @Before
    fun setUp() {
        useCase = LogMealUseCase(mealRepository, foodRepository)
    }

    @Test
    fun logSingleFoodPortion_calculatesMacrosAndInserts() = runTest {
        val food = Food(id = 1, name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f)
        val result = useCase.logSingleFoodPortion(food, 150f)

        assertTrue(result is Result.Success)
        val insertedId = (result as Result.Success).data
        assertEquals(1L, insertedId)
        
        // Verify frequency increment
        assertEquals(1, foodRepository.getFrequency(1L))
    }

    @Test
    fun invoke_incrementsFrequencyForEachPortion() = runTest {
        val portions = listOf(
            LoggedFoodPortion("Apple", 52, 0.3f, 13.8f, 0.2f, 100f),
            LoggedFoodPortion("Banana", 89, 1.1f, 22.8f, 0.3f, 100f)
        )
        val result = useCase("Fruit Salad", portions)

        assertTrue(result is Result.Success)
        assertEquals(1, foodRepository.getFrequencyByName("Apple"))
        assertEquals(1, foodRepository.getFrequencyByName("Banana"))
    }
}
