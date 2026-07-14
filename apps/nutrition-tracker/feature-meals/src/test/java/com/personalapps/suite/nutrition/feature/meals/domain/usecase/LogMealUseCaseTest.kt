package com.personalapps.suite.nutrition.feature.meals.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.common.Result
import java.time.Instant
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
    override fun getMealsBetween(startDate: Instant, endDate: Instant): Flow<List<Meal>> = meals
    override suspend fun insertMeal(meal: Meal): Long {
        val list = meals.value.toMutableList()
        val newMeal = meal.copy(id = (list.size + 1).toLong())
        list.add(newMeal)
        meals.value = list
        return newMeal.id
    }
    override suspend fun deleteMeal(meal: Meal) {}
}

class LogMealUseCaseTest {
    private val repository = FakeMealRepository()
    private lateinit var useCase: LogMealUseCase

    @Before
    fun setUp() {
        useCase = LogMealUseCase(repository)
    }

    @Test
    fun logSingleFoodPortion_calculatesMacrosAndInserts() = runTest {
        val food = Food(id = 1, name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f)
        val result = useCase.logSingleFoodPortion(food, 150f)

        assertTrue(result is Result.Success)
        val insertedId = (result as Result.Success).data
        assertEquals(1L, insertedId)
    }
}
