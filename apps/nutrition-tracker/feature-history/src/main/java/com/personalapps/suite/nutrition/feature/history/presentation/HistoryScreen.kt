package com.personalapps.suite.nutrition.feature.history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.PersonalScaffold

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToFood: () -> Unit,
    onNavigateToLogMeal: () -> Unit,
    onNavigateToConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.dashboardState.collectAsState()

    val totalCalories = remember(state.meals) {
        state.meals.sumOf { meal -> meal.loggedFoods.sumOf { it.calories } }
    }

    val totalProtein = remember(state.meals) {
        state.meals.sumOf { meal -> meal.loggedFoods.sumOf { it.protein.toDouble() } }.toFloat()
    }

    val totalCarbs = remember(state.meals) {
        state.meals.sumOf { meal -> meal.loggedFoods.sumOf { it.carbs.toDouble() } }.toFloat()
    }

    val totalFat = remember(state.meals) {
        state.meals.sumOf { meal -> meal.loggedFoods.sumOf { it.fat.toDouble() } }.toFloat()
    }

    val targetCalories = state.goal?.calories ?: 2000
    val targetProtein = state.goal?.protein ?: 120f
    val targetCarbs = state.goal?.carbs ?: 200f
    val targetFat = state.goal?.fat ?: 70f

    PersonalScaffold(
        title = "Nutrition Dashboard",
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToLogMeal) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Log Meal")
            }
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            PersonalCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Today's Calories", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$totalCalories / $targetCalories kcal",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (targetCalories > 0) (totalCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f) else 0f
                        },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PersonalCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Macronutrients",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    MacroProgressRow(
                        label = "Protein",
                        current = totalProtein,
                        target = targetProtein,
                        unit = "g",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    MacroProgressRow(
                        label = "Carbs",
                        current = totalCarbs,
                        target = targetCarbs,
                        unit = "g",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    MacroProgressRow(
                        label = "Fat",
                        current = totalFat,
                        target = targetFat,
                        unit = "g",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Today's Meals", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (state.meals.isEmpty()) {
                Text(
                    text = "No meals logged today yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(state.meals) { meal ->
                        val kcal = meal.loggedFoods.sumOf { it.calories }
                        PersonalCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(text = meal.name, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        text = meal.loggedFoods.joinToString(", ") { it.name },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                Text(text = "$kcal kcal", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroProgressRow(
    label: String,
    current: Float,
    target: Float,
    unit: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${"%.1f".format(current)} / ${"%.1f".format(target)} $unit",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = {
                if (target > 0f) (current / target).coerceIn(0f, 1f) else 0f
            },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
