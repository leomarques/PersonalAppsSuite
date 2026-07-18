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
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.NutrientPortionDialog
import com.personalapps.suite.shared.designsystem.proteinColor
import com.personalapps.suite.shared.designsystem.carbsColor
import com.personalapps.suite.shared.designsystem.fatColor
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.SwipeActionContainer

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToLogMeal: () -> Unit,
    onNavigateToConfig: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showConfirmDialog by remember { mutableStateOf(value = false) }
    var editingMealAndPortion by remember { mutableStateOf<Pair<Meal, LoggedFoodPortion>?>(null) }


    val daySavedHistory = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.day_saved_history)

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HistoryEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is HistoryEffect.DayStarted -> snackbarHostState.showSnackbar(daySavedHistory)
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(com.personalapps.suite.nutrition.feature.history.R.string.start_new_day)) },
            text = { Text(stringResource(com.personalapps.suite.nutrition.feature.history.R.string.start_new_day_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.startNewDay()
                        showConfirmDialog = false
                    }
                ) {
                    Text(stringResource(com.personalapps.suite.nutrition.feature.history.R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(stringResource(com.personalapps.suite.nutrition.feature.history.R.string.cancel))
                }
            },
        )
    }

    val totalCalories = state.totalCalories
    val totalProtein = state.totalProtein
    val totalCarbs = state.totalCarbs
    val totalFat = state.totalFat

    val targetCalories = state.goal?.calories ?: 2000
    val targetProtein = state.goal?.protein ?: 120f
    val targetCarbs = state.goal?.carbs ?: 200f
    val targetFat = state.goal?.fat ?: 70f

    PersonalScaffold(
        title = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.title),
        actions = {
            if (state.meals.isNotEmpty()) {
                IconButton(onClick = { showConfirmDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.start_new_day)
                    )
                }
            }
            IconButton(onClick = onNavigateToHistory) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.history)
                )
            }
            IconButton(onClick = onNavigateToConfig) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.settings)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToLogMeal) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.log_meal))
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
                    Text(text = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.todays_calories), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.calories_ratio, totalCalories, targetCalories),
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
                        text = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.macronutrients),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    MacroProgressRow(
                        label = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.protein),
                        current = totalProtein,
                        target = targetProtein,
                        unit = "g",
                        color = proteinColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    MacroProgressRow(
                        label = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.carbs),
                        current = totalCarbs,
                        target = targetCarbs,
                        unit = "g",
                        color = carbsColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    MacroProgressRow(
                        label = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.fat),
                        current = totalFat,
                        target = targetFat,
                        unit = "g",
                        color = fatColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.todays_meals), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (state.meals.isEmpty()) {
                Text(
                    text = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.no_meals_logged),
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
                            SwipeActionContainer(
                                onDelete = { viewModel.deleteMeal(meal) },
                                confirmTitle = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.delete_meal),
                                confirmMessage = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.delete_meal_confirm_message, portion.name)
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
            title = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.edit_meal_title, portion.name),
            proteinPerServing = (portion.protein / (portion.amountGrams / portion.gramsPerServing)),
            carbsPerServing = (portion.carbs / (portion.amountGrams / portion.gramsPerServing)),
            fatPerServing = (portion.fat / (portion.amountGrams / portion.gramsPerServing)),
            caloriesPerServing = (portion.calories.toFloat() / (portion.amountGrams / portion.gramsPerServing)).toInt(),
            initialAmountGrams = portion.amountGrams,
            gramsPerServing = portion.gramsPerServing,
            onDismiss = { editingMealAndPortion = null },
            onConfirm = { newAmount ->
                viewModel.updateMealPortion(meal, portion, newAmount)
                editingMealAndPortion = null
            },
            confirmLabel = stringResource(com.personalapps.suite.nutrition.feature.history.R.string.update)
        )
    }
}


@Composable
fun LoggedFoodItem(
    portion: LoggedFoodPortion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val amountText = if ((portion.amountGrams % portion.gramsPerServing) == 0f) {
        val servings = (portion.amountGrams / portion.gramsPerServing).toInt()
        stringResource(
            com.personalapps.suite.nutrition.feature.history.R.string.servings_amount,
            servings,
            if (servings == 1) stringResource(com.personalapps.suite.nutrition.feature.history.R.string.serving) else stringResource(com.personalapps.suite.nutrition.feature.history.R.string.servings_plural)
        )
    } else {
        stringResource(com.personalapps.suite.nutrition.feature.history.R.string.grams_amount, portion.amountGrams.toInt())
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
                    NutrientText(label = stringResource(com.personalapps.suite.shared.uicomponents.R.string.nutrient_protein_short), value = portion.protein, color = proteinColor)
                    Bullet()
                    NutrientText(label = stringResource(com.personalapps.suite.shared.uicomponents.R.string.nutrient_carbs_short), value = portion.carbs, color = carbsColor)
                    Bullet()
                    NutrientText(label = stringResource(com.personalapps.suite.shared.uicomponents.R.string.nutrient_fat_short), value = portion.fat, color = fatColor)
                }
            }
            Text(
                text = stringResource(com.personalapps.suite.shared.uicomponents.R.string.calories_kcal, portion.calories),
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
    val formattedValue = if ((value % 1) == 0f) value.toInt().toString() else "%.1f".format(value)
    Text(
        text = stringResource(com.personalapps.suite.shared.uicomponents.R.string.nutrient_value_grams, label, formattedValue),
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
