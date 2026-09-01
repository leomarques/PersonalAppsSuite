package com.personalapps.suite.nutrition.feature.meals.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.common.DateProvider
import com.personalapps.suite.shared.common.Result
import com.personalapps.suite.shared.databaseutils.TransactionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class FakeFoodRepository : FoodRepository {
    private val lastUsedUpdates = mutableMapOf<Long, Int>()
    private val nameLastUsedUpdates = mutableMapOf<String, Int>()

    fun getUpdateCount(id: Long) = lastUsedUpdates[id] ?: 0
    fun getUpdateCountByName(name: String) = nameLastUsedUpdates[name] ?: 0

    override fun getAllFoods(): Flow<List<Food>> = emptyFlow()
    override suspend fun insertFood(food: Food): Result<Long> = Result.Success(0L)
    override suspend fun updateFood(food: Food): Result<Unit> = Result.Success(Unit)
    override suspend fun deleteFood(food: Food): Result<Unit> = Result.Success(Unit)
    override suspend fun updateLastUsed(foodId: Long): Result<Unit> {
        lastUsedUpdates[foodId] = (lastUsedUpdates[foodId] ?: 0) + 1
        return Result.Success(Unit)
    }
    override suspend fun updateLastUsedByName(name: String): Result<Unit> {
        nameLastUsedUpdates[name] = (nameLastUsedUpdates[name] ?: 0) + 1
        return Result.Success(Unit)
    }
}

class FakeMealRepository : MealRepository {
    private val meals = mutableListOf<Meal>()
    override fun getAllMeals(): Flow<List<Meal>> = emptyFlow()
    override suspend fun insertMeal(meal: Meal): Result<Long> {
        meals.add(meal)
        return Result.Success(meals.size.toLong())
    }
    override suspend fun deleteMeal(meal: Meal): Result<Unit> = Result.Success(Unit)
}

class FakeDateProvider : DateProvider {
    override fun now(): LocalDate = LocalDate.EPOCH
    override fun nowInstant(): Instant = Instant.EPOCH
}

class FakeTransactionProvider : TransactionProvider {
    override suspend fun <T> runInTransaction(block: suspend () -> T): T = block()
}

class LogMealUseCaseTest {

    private lateinit var useCase: LogMealUseCase
    private val mealRepository = FakeMealRepository()
    private val foodRepository = FakeFoodRepository()
    private val dateProvider = FakeDateProvider()
    private val transactionProvider = FakeTransactionProvider()

    @Before
    fun setUp() {
        useCase = LogMealUseCase(mealRepository, foodRepository, dateProvider, transactionProvider)
    }

    @Test
    fun logSingleFoodPortion_updatesLastUsed() = runTest {
        val food = Food(id = 1L, name = "Apple", calories = 52, protein = 0.3f, carbs = 13.8f, fat = 0.2f)
        
        useCase.logSingleFoodPortion(food, 100f)
        
        // Verify lastUsed update
        assertEquals(1, foodRepository.getUpdateCount(1L))
    }

    @Test
    fun invoke_updatesLastUsedForEachPortion() = runTest {
        val portions = listOf(
            LoggedFoodPortion("Apple", 52, 0.3f, 13.8f, 0.2f, 100f),
            LoggedFoodPortion("Banana", 89, 1.1f, 22.8f, 0.3f, 100f)
        )
        
        useCase("Lunch", portions)
        
        assertEquals(1, foodRepository.getUpdateCountByName("Apple"))
        assertEquals(1, foodRepository.getUpdateCountByName("Banana"))
    }
}
