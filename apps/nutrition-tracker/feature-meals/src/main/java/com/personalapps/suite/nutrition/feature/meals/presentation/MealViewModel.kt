package com.personalapps.suite.nutrition.feature.meals.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.food.domain.model.Food
import com.personalapps.suite.nutrition.feature.food.domain.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.meals.domain.model.Meal
import com.personalapps.suite.nutrition.feature.meals.domain.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.meals.domain.repository.MealRepository
import java.time.Instant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MealViewModel(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    val foodsState: StateFlow<List<Food>> = foodRepository.getAllFoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mealsState: StateFlow<List<Meal>> = mealRepository.getAllMeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun logMeal(name: String, portions: List<LoggedFoodPortion>) {
        if (portions.isEmpty() || name.isBlank()) return
        viewModelScope.launch {
            mealRepository.insertMeal(
                Meal(
                    name = name,
                    timestamp = Instant.now(),
                    loggedFoods = portions
                )
            )
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.deleteMeal(meal)
        }
    }

    fun addCustomFood(name: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        if (name.isBlank()) return
        viewModelScope.launch {
            foodRepository.insertFood(
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

    fun logSingleFoodPortion(food: Food, amountGrams: Float, mealType: String) {
        if (amountGrams <= 0f) return
        viewModelScope.launch {
            val factor = amountGrams / 100f
            val portion = LoggedFoodPortion(
                name = food.name,
                calories = (food.calories * factor).toInt(),
                protein = food.protein * factor,
                carbs = food.carbs * factor,
                fat = food.fat * factor,
                amountGrams = amountGrams
            )
            mealRepository.insertMeal(
                Meal(
                    name = mealType,
                    timestamp = Instant.now(),
                    loggedFoods = listOf(portion)
                )
            )
        }
    }
}
