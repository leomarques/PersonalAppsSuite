package com.personalapps.suite.nutrition.feature.meals.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.nutrition.feature.meals.domain.usecase.LogMealUseCase
import com.personalapps.suite.shared.common.Result
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import kotlinx.coroutines.launch

data class MealUiState(
    val foods: List<Food> = emptyList(),
    val meals: List<Meal> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface MealEffect {
    data class ShowError(val message: String) : MealEffect
    data object MealLogged : MealEffect
    data object MealDeleted : MealEffect
    data object FoodAdded : MealEffect
}

class MealViewModel(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
    private val logMealUseCase: LogMealUseCase
) : BaseViewModel<MealUiState, MealEffect>(MealUiState()) {

    init {
        viewModelScope.launch {
            foodRepository.getAllFoods().collect { foods ->
                updateState { copy(foods = foods, isLoading = false) }
            }
        }
        viewModelScope.launch {
            mealRepository.getAllMeals().collect { meals ->
                updateState { copy(meals = meals, isLoading = false) }
            }
        }
    }

    fun logMeal(name: String, portions: List<LoggedFoodPortion>) {
        if (portions.isEmpty() || name.isBlank()) return
        viewModelScope.launch {
            when (val result = logMealUseCase(name, portions)) {
                is Result.Success -> sendEffect(MealEffect.MealLogged)
                is Result.Error -> sendEffect(MealEffect.ShowError(result.exception.message ?: "Failed to log meal"))
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            try {
                mealRepository.deleteMeal(meal)
                sendEffect(MealEffect.MealDeleted)
            } catch (e: Exception) {
                sendEffect(MealEffect.ShowError(e.message ?: "Failed to delete meal"))
            }
        }
    }

    fun addCustomFood(name: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try {
                foodRepository.insertFood(
                    Food(
                        name = name,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat
                    )
                )
                sendEffect(MealEffect.FoodAdded)
            } catch (e: Exception) {
                sendEffect(MealEffect.ShowError(e.message ?: "Failed to add food"))
            }
        }
    }

    fun logSingleFoodPortion(food: Food, amountGrams: Float, mealType: String) {
        if (amountGrams <= 0f) return
        viewModelScope.launch {
            when (val result = logMealUseCase.logSingleFoodPortion(food, amountGrams, mealType)) {
                is Result.Success -> sendEffect(MealEffect.MealLogged)
                is Result.Error -> sendEffect(MealEffect.ShowError(result.exception.message ?: "Failed to log portion"))
                is Result.Loading -> { /* no-op */ }
            }
        }
    }
}
