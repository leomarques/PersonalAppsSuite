package com.personalapps.suite.nutrition.feature.macros.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.shared.uicomponents.base.BaseViewModel
import kotlinx.coroutines.launch

data class MacroUiState(
    val goal: MacroGoal? = null,
    val isLoading: Boolean = true
)

sealed interface MacroEffect {
    data class ShowError(val message: String) : MacroEffect
    data object GoalSaved : MacroEffect
}

class MacroViewModel(private val repository: MacroGoalRepository) : BaseViewModel<MacroUiState, MacroEffect>(MacroUiState()) {

    init {
        viewModelScope.launch {
            repository.getMacroGoal().collect { goal ->
                updateState { copy(goal = goal, isLoading = false) }
            }
        }
    }

    fun saveMacroGoal(calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            try {
                repository.insertMacroGoal(
                    MacroGoal(
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat
                    )
                )
                sendEffect(MacroEffect.GoalSaved)
            } catch (e: Exception) {
                sendEffect(MacroEffect.ShowError(e.message ?: "Failed to save goal"))
            }
        }
    }
}
