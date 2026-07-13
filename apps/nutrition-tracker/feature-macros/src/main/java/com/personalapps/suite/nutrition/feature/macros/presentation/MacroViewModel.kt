package com.personalapps.suite.nutrition.feature.macros.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalapps.suite.nutrition.feature.macros.domain.model.MacroGoal
import com.personalapps.suite.nutrition.feature.macros.domain.repository.MacroGoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MacroViewModel(private val repository: MacroGoalRepository) : ViewModel() {
    val macroGoal: StateFlow<MacroGoal?> = repository.getMacroGoal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveMacroGoal(calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            repository.insertMacroGoal(
                MacroGoal(
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat
                )
            )
        }
    }
}
