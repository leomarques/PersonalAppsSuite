package com.personalapps.suite.nutrition.feature.history.domain.usecase

import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.repository.HistoryRepository
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.common.DateProvider
import com.personalapps.suite.shared.common.Result
import com.personalapps.suite.shared.databaseutils.TransactionProvider
import com.personalapps.suite.shared.preferences.PreferencesManager
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class StartNewDayUseCase(
    private val mealRepository: MealRepository,
    private val macroGoalRepository: MacroGoalRepository,
    private val historyRepository: HistoryRepository,
    private val preferencesManager: PreferencesManager,
    private val dateProvider: DateProvider,
    private val transactionProvider: TransactionProvider
) {
    suspend operator fun invoke(meals: List<Meal>, goal: MacroGoal?): Result<Unit> = try {
        transactionProvider.runInTransaction {
            if (meals.isEmpty()) return@runInTransaction

            val storedDateString = preferencesManager.getOpenDayDate().first()
            val dateToSave = storedDateString?.let { LocalDate.parse(it) } ?: dateProvider.now()

            val totalCalories = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.calories } }
            val totalProtein = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.protein.toDouble() } }.toFloat()
            val totalCarbs = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.carbs.toDouble() } }.toFloat()
            val totalFat = meals.sumOf { meal -> meal.loggedFoods.sumOf { it.fat.toDouble() } }.toFloat()

            val existingEntryResult = historyRepository.getHistoryEntryByDate(dateToSave)
            val existingEntry = (existingEntryResult as? Result.Success)?.data
            
            val historyEntry = HistoryEntry(
                date = dateToSave,
                totalCalories = totalCalories + (existingEntry?.totalCalories ?: 0),
                totalProtein = totalProtein + (existingEntry?.totalProtein ?: 0f),
                totalCarbs = totalCarbs + (existingEntry?.totalCarbs ?: 0f),
                totalFat = totalFat + (existingEntry?.totalFat ?: 0f),
                goalCalories = goal?.calories ?: 0,
                goalProtein = goal?.protein ?: 0f,
                goalCarbs = goal?.carbs ?: 0f,
                goalFat = goal?.fat ?: 0f
            )

            historyRepository.insertHistoryEntry(historyEntry)

            // Clear the list by deleting the meals
            meals.forEach { meal ->
                mealRepository.deleteMeal(meal)
            }

            // Set new open day date to today
            preferencesManager.setOpenDayDate(dateProvider.now().toString())
        }
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
