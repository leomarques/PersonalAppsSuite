package com.personalapps.suite.nutrition.feature.macros.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personalapps.suite.shared.designsystem.PersonalButton
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField

@Composable
fun MacroScreen(
    viewModel: MacroViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentGoal by viewModel.macroGoal.collectAsState()

    var caloriesStr by remember { mutableStateOf("") }
    var proteinStr by remember { mutableStateOf("") }
    var carbsStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }

    LaunchedEffect(currentGoal) {
        currentGoal?.let {
            caloriesStr = it.calories.toString()
            proteinStr = it.protein.toString()
            carbsStr = it.carbs.toString()
            fatStr = it.fat.toString()
        } ?: run {
            caloriesStr = "2000"
            proteinStr = "120"
            carbsStr = "200"
            fatStr = "70"
        }
    }

    PersonalScaffold(
        title = "Macro Targets",
        onBackClick = onBackClick,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Set Daily Macro Targets",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Configure your target daily nutrient intake. These targets are used to track your nutrition progress on the main dashboard.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            PersonalCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PersonalTextField(
                        value = caloriesStr,
                        onValueChange = { caloriesStr = it },
                        label = "Target Calories (kcal)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PersonalTextField(
                        value = proteinStr,
                        onValueChange = { proteinStr = it },
                        label = "Target Protein (g)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PersonalTextField(
                        value = carbsStr,
                        onValueChange = { carbsStr = it },
                        label = "Target Carbs (g)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PersonalTextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = "Target Fat (g)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    PersonalButton(
                        text = "Save Targets",
                        onClick = {
                            val calories = caloriesStr.toIntOrNull() ?: 2000
                            val protein = proteinStr.toFloatOrNull() ?: 120f
                            val carbs = carbsStr.toFloatOrNull() ?: 200f
                            val fat = fatStr.toFloatOrNull() ?: 70f
                            viewModel.saveMacroGoal(calories, protein, carbs, fat)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
