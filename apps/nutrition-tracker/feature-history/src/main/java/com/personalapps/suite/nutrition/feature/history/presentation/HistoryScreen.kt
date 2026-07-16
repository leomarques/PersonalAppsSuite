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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personalapps.suite.nutrition.feature.api.model.LoggedFoodPortion
import com.personalapps.suite.nutrition.feature.api.model.Meal
import com.personalapps.suite.nutrition.feature.history.R
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.NutrientPortionDialog
import com.personalapps.suite.shared.designsystem.proteinColor
import com.personalapps.suite.shared.designsystem.carbsColor
import com.personalapps.suite.shared.designsystem.fatColor
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.SwipeToDeleteContainer

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToLogMeal: () -> Unit,
    onNavigateToConfig: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var editingMealAndPortion by remember { mutableStateOf<Pair<Meal, LoggedFoodPortion>?>(null) }


    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HistoryEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is HistoryEffect.DayStarted -> snackbarHostState.showSnackbar("Day saved to history!")
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Start New Day") },
            text = { Text("This will save your current totals to history and clear today's meals. Are you sure?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.startNewDay()
                    showConfirmDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
        title = stringResource(R.string.title),
        actions = {
            if (state.meals.isNotEmpty()) {
                IconButton(onClick = { showConfirmDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Start New Day"
                    )
                }
            }
            IconButton(onClick = onNavigateToHistory) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "History"
                )
            }
            IconButton(onClick = onNavigateToConfig) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToLogMeal) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Log Meal")
            }
        },
        snackbarHostState = snackbarHostState,
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
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
                        color = proteinColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    MacroProgressRow(
                        label = "Carbs",
                        current = totalCarbs,
                        target = targetCarbs,
                        unit = "g",
                        color = carbsColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    MacroProgressRow(
                        label = "Fat",
                        current = totalFat,
                        target = targetFat,
                        unit = "g",
                        color = fatColor
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    state.meals.forEach { meal ->
                        items(
                            items = meal.loggedFoods,
                            key = { "${meal.id}-${it.name}" }
                        ) { portion ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.deleteMeal(meal) },
                                confirmTitle = "Delete Meal",
                                confirmMessage = "Are you sure you want to delete '${portion.name}'?"
                            ) {
                                LoggedFoodItem(
                                    portion = portion,
                                    onClick = { editingMealAndPortion = meal to portion }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    editingMealAndPortion?.let { (meal, portion) ->
        NutrientPortionDialog(
            title = "Edit ${portion.name}",
            proteinPer100g = (portion.protein / portion.amountGrams) * 100f,
            carbsPer100g = (portion.carbs / portion.amountGrams) * 100f,
            fatPer100g = (portion.fat / portion.amountGrams) * 100f,
            caloriesPer100g = ((portion.calories.toFloat() / portion.amountGrams) * 100f).toInt(),
            initialAmountGrams = portion.amountGrams,
            onDismiss = { editingMealAndPortion = null },
            onConfirm = { newAmount ->
                viewModel.updateMealPortion(meal, portion, newAmount)
                editingMealAndPortion = null
            },
            confirmLabel = "Update"
        )
    }
}


@Composable
fun LoggedFoodItem(
    portion: LoggedFoodPortion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val amountText = if (portion.amountGrams % 100f == 0f) {
        "${(portion.amountGrams / 100f).toInt()} servings"
    } else {
        "${portion.amountGrams.toInt()}g"
    }

    PersonalCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = portion.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = amountText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NutrientText(label = "P", value = portion.protein, color = proteinColor)
                    Bullet()
                    NutrientText(label = "C", value = portion.carbs, color = carbsColor)
                    Bullet()
                    NutrientText(label = "F", value = portion.fat, color = fatColor)
                }
            }
            Text(
                text = "${portion.calories} kcal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun NutrientText(label: String, value: Float, color: Color) {
    val formattedValue = if (value % 1 == 0f) value.toInt().toString() else "%.1f".format(value)
    Text(
        text = "$label: ${formattedValue}g",
        style = MaterialTheme.typography.bodySmall,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun Bullet() {
    Text(
        text = "•",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
fun MacroProgressRow(
    label: String,
    current: Float,
    target: Float,
    unit: String,
    color: Color,
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
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
