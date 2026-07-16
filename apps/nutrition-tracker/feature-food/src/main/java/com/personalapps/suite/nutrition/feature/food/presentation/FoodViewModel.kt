package com.personalapps.suite.nutrition.feature.food.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class FoodUiState(
    val foods: List<Food> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface FoodEffect {
    data class ShowError(val message: String) : FoodEffect
    data object FoodAdded : FoodEffect
    data object FoodDeleted : FoodEffect
}

class FoodViewModel(
    private val repository: FoodRepository,
    private val mealRepository: MealRepository
) : BaseViewModel<FoodUiState, FoodEffect>(FoodUiState()) {

    init {
        viewModelScope.launch {
            combine(
                repository.getAllFoods(),
                mealRepository.getAllMeals()
            ) { foods, meals ->
                val frequencyMap = meals.flatMap { it.loggedFoods }
                    .groupingBy { it.name }
                    .eachCount()

                foods.sortedWith(
                    compareByDescending<Food> { frequencyMap[it.name] ?: 0 }
                        .thenBy { it.name }
                )
            }.collect { sortedFoods ->
                updateState { copy(foods = sortedFoods, isLoading = false) }
            }
        }
    }

    fun addFood(name: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            try {
                repository.insertFood(
                    Food(
                        name = name,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat
                    )
                )
                sendEffect(FoodEffect.FoodAdded)
            } catch (e: Exception) {
                sendEffect(FoodEffect.ShowError(e.message ?: "Failed to add food"))
            }
        }
    }

    fun deleteFood(food: Food) {
        viewModelScope.launch {
            try {
                repository.deleteFood(food)
                sendEffect(FoodEffect.FoodDeleted)
            } catch (e: Exception) {
                sendEffect(FoodEffect.ShowError(e.message ?: "Failed to delete food"))
            }
        }
    }
}
