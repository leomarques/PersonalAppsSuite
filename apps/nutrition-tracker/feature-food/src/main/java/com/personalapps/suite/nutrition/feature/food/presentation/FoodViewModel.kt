package com.personalapps.suite.nutrition.feature.food.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.food.domain.usecase.AddFoodUseCase
import com.personalapps.suite.nutrition.feature.food.domain.usecase.DeleteFoodUseCase
import com.personalapps.suite.nutrition.feature.food.domain.usecase.UpdateFoodUseCase
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import kotlinx.coroutines.launch

data class FoodUiState(
    val foods: List<Food> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface FoodEffect {
    data class ShowError(val message: String) : FoodEffect
    data object FoodAdded : FoodEffect
    data object FoodUpdated : FoodEffect
    data object FoodDeleted : FoodEffect
}

class FoodViewModel(
    private val repository: FoodRepository,
    private val addFoodUseCase: AddFoodUseCase,
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase
) : BaseViewModel<FoodUiState, FoodEffect>(FoodUiState()) {

    init {
        viewModelScope.launch {
            repository.getAllFoods().collect { foods ->
                updateState { copy(foods = foods, isLoading = false) }
            }
        }
    }

    fun addFood(name: String, caloriesStr: String, proteinStr: String, carbsStr: String, fatStr: String) {
        viewModelScope.launch {
            val calories = caloriesStr.toIntOrNull() ?: 0
            val protein = proteinStr.toFloatOrNull() ?: 0f
            val carbs = carbsStr.toFloatOrNull() ?: 0f
            val fat = fatStr.toFloatOrNull() ?: 0f

            val result = addFoodUseCase(name, calories, protein, carbs, fat)
            handleResult(
                result = result,
                onSuccess = { sendEffect(FoodEffect.FoodAdded) },
                onError = { sendEffect(FoodEffect.ShowError(it.message ?: "Failed to add food")) }
            )
        }
    }

    fun updateFood(id: Long, name: String, caloriesStr: String, proteinStr: String, carbsStr: String, fatStr: String) {
        viewModelScope.launch {
            val calories = caloriesStr.toIntOrNull() ?: 0
            val protein = proteinStr.toFloatOrNull() ?: 0f
            val carbs = carbsStr.toFloatOrNull() ?: 0f
            val fat = fatStr.toFloatOrNull() ?: 0f

            val result = updateFoodUseCase(id, name, calories, protein, carbs, fat)
            handleResult(
                result = result,
                onSuccess = { sendEffect(FoodEffect.FoodUpdated) },
                onError = { sendEffect(FoodEffect.ShowError(it.message ?: "Failed to update food")) }
            )
        }
    }

    fun deleteFood(food: Food) {
        viewModelScope.launch {
            val result = deleteFoodUseCase(food)
            handleResult(
                result = result,
                onSuccess = { sendEffect(FoodEffect.FoodDeleted) },
                onError = { sendEffect(FoodEffect.ShowError(it.message ?: "Failed to delete food")) }
            )
        }
    }
}
