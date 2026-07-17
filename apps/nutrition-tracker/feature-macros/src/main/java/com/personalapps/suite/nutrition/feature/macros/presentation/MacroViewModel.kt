package com.personalapps.suite.nutrition.feature.macros.presentation

import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.macros.domain.usecase.SaveMacroGoalUseCase
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

class MacroViewModel(
    private val repository: MacroGoalRepository,
    private val saveMacroGoalUseCase: SaveMacroGoalUseCase
) : BaseViewModel<MacroUiState, MacroEffect>(MacroUiState()) {

    init {
        viewModelScope.launch {
            repository.getMacroGoal().collect { goal ->
                updateState { copy(goal = goal, isLoading = false) }
            }
        }
    }

    fun saveMacroGoal(caloriesStr: String, proteinStr: String, carbsStr: String, fatStr: String) {
        viewModelScope.launch {
            val calories = caloriesStr.toIntOrNull() ?: 0
            val protein = proteinStr.toFloatOrNull() ?: 0f
            val carbs = carbsStr.toFloatOrNull() ?: 0f
            val fat = fatStr.toFloatOrNull() ?: 0f

            val result = saveMacroGoalUseCase(calories, protein, carbs, fat)
            handleResult(
                result = result,
                onSuccess = { sendEffect(MacroEffect.GoalSaved) },
                onError = { sendEffect(MacroEffect.ShowError(it.message ?: "Failed to save goal")) }
            )
        }
    }
}
