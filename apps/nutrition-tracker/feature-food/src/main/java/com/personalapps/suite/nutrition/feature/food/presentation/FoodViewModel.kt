package com.personalapps.suite.nutrition.feature.food.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.food.domain.model.Food
import com.personalapps.suite.nutrition.feature.food.domain.repository.FoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodViewModel(private val repository: FoodRepository) : ViewModel() {
    val foodsState: StateFlow<List<Food>> = repository.getAllFoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFood(name: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            repository.insertFood(
                Food(
                    name = name,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat
                )
            )
        }
    }

    fun deleteFood(food: Food) {
        viewModelScope.launch {
            repository.deleteFood(food)
        }
    }
}
