package com.personalapps.suite.nutrition.feature.meals.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.api.model.Meal
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
    data class FoodAdded(val food: Food) : MealEffect
    data object FoodUpdated : MealEffect
    data object FoodDeleted : MealEffect
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

    fun addCustomFood(name: String, calories: Int, protein: Float, carbs: Float, fat: Float, gramsPerServing: Float) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val food = Food(
                name = name,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat,
                gramsPerServing = gramsPerServing
            )
            when (val result = foodRepository.insertFood(food)) {
                is Result.Success -> sendEffect(MealEffect.FoodAdded(food.copy(id = result.data)))
                is Result.Error -> sendEffect(MealEffect.ShowError(result.exception.message ?: "Failed to add food"))
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    fun updateFood(id: Long, name: String, calories: Int, protein: Float, carbs: Float, fat: Float, gramsPerServing: Float) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try {
                foodRepository.updateFood(
                    Food(
                        id = id,
                        name = name,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        gramsPerServing = gramsPerServing
                    )
                )
                sendEffect(MealEffect.FoodUpdated)
            } catch (e: Exception) {
                sendEffect(MealEffect.ShowError(e.message ?: "Failed to update food"))
            }
        }
    }

    fun logSingleFoodPortion(food: Food, amountGrams: Float) {
        if (amountGrams <= 0f) return
        viewModelScope.launch {
            when (val result = logMealUseCase.logSingleFoodPortion(food, amountGrams)) {
                is Result.Success -> sendEffect(MealEffect.MealLogged)
                is Result.Error -> sendEffect(MealEffect.ShowError(result.exception.message ?: "Failed to log portion"))
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    fun deleteFood(food: Food) {
        viewModelScope.launch {
            try {
                foodRepository.deleteFood(food)
                sendEffect(MealEffect.FoodDeleted)
            } catch (e: Exception) {
                sendEffect(MealEffect.ShowError(e.message ?: "Failed to delete food"))
            }
        }
    }
}
