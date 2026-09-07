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
    val selectedFoodIds: Set<Long> = emptySet(),
    val isMultiSelectMode: Boolean = false,
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
        if (name.isBlank()) {
            sendEffect(MealEffect.ShowError("Food name cannot be empty"))
            return
        }
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

    fun updateFood(food: Food) {
        if (food.name.isBlank()) {
            sendEffect(MealEffect.ShowError("Food name cannot be empty"))
            return
        }
        viewModelScope.launch {
            try {
                foodRepository.updateFood(food)
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

    fun toggleFoodSelection(foodId: Long) {
        updateState {
            val newSelected = if (selectedFoodIds.contains(foodId)) {
                selectedFoodIds - foodId
            } else {
                selectedFoodIds + foodId
            }
            copy(
                selectedFoodIds = newSelected,
                isMultiSelectMode = newSelected.isNotEmpty() || isMultiSelectMode
            )
        }
    }

    fun setMultiSelectMode(enabled: Boolean) {
        updateState {
            copy(
                isMultiSelectMode = enabled,
                selectedFoodIds = if (enabled) selectedFoodIds else emptySet()
            )
        }
    }

    fun clearSelection() {
        updateState { copy(selectedFoodIds = emptySet(), isMultiSelectMode = false) }
    }

    fun logMixedMeal(name: String, foodPortions: List<Pair<Food, Float>>) {
        if (name.isBlank()) {
            sendEffect(MealEffect.ShowError("Food name cannot be empty"))
            return
        }
        if (foodPortions.isEmpty()) return
        viewModelScope.launch {
            var totalCalories = 0
            var totalProtein = 0f
            var totalCarbs = 0f
            var totalFat = 0f
            var totalWeight = 0f

            foodPortions.forEach { (food, amountGrams) ->
                val factor = amountGrams / food.gramsPerServing
                totalCalories += (food.calories * factor).toInt()
                totalProtein += food.protein * factor
                totalCarbs += food.carbs * factor
                totalFat += food.fat * factor
                totalWeight += amountGrams
            }

            val aggregateFood = Food(
                name = name,
                calories = totalCalories,
                protein = totalProtein,
                carbs = totalCarbs,
                fat = totalFat,
                gramsPerServing = totalWeight
            )

            when (val result = foodRepository.insertFood(aggregateFood)) {
                is Result.Success -> {
                    val newFood = aggregateFood.copy(id = result.data)
                    clearSelection()
                    sendEffect(MealEffect.FoodAdded(newFood))
                }
                is Result.Error -> sendEffect(MealEffect.ShowError(result.exception.message ?: "Failed to save mixed food"))
                else -> {}
            }
        }
    }
}
