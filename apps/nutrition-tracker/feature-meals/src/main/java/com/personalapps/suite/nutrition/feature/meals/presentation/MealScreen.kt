package com.personalapps.suite.nutrition.feature.meals.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.uicomponents.NutrientListItem
import com.personalapps.suite.shared.uicomponents.NutrientPortionDialog
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField
import com.personalapps.suite.shared.uicomponents.SwipeToDeleteContainer

@Composable
fun MealScreen(
    viewModel: MealViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MealEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is MealEffect.MealLogged -> snackbarHostState.showSnackbar("Meal logged successfully")
                is MealEffect.MealDeleted -> snackbarHostState.showSnackbar("Meal deleted")
                is MealEffect.FoodAdded -> snackbarHostState.showSnackbar("Custom food added to database")
                is MealEffect.FoodDeleted -> snackbarHostState.showSnackbar("Food deleted from library")
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var showAddFoodDialog by remember { mutableStateOf(false) }
    var selectedFoodToLog by remember { mutableStateOf<Food?>(null) }

    val filteredFoods = remember(state.foods, searchQuery) {
        if (searchQuery.isBlank()) {
            state.foods
        } else {
            state.foods.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    PersonalScaffold(
        title = "Add Entry",
        onBackClick = onBackClick,
        actions = {
            IconButton(onClick = { showAddFoodDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Food"
                )
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
            PersonalTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Search foods...",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                } else null,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Food Database Library",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredFoods.isEmpty()) {
                val msg = if (searchQuery.isBlank()) {
                    "Your food database is empty. Create a custom food item using the add icon in the top bar to start logging!"
                } else {
                    "No matching foods found."
                }
                EmptyScreen(message = msg)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredFoods) { food ->
                        SwipeToDeleteContainer(
                            onDelete = { viewModel.deleteFood(food) },
                            confirmTitle = "Delete Food",
                            confirmMessage = "Are you sure you want to delete '${food.name}' from the library?"
                        ) {
                            FoodListItem(
                                food = food,
                                onClick = { selectedFoodToLog = food }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Custom Food Dialog
    if (showAddFoodDialog) {
        AddFoodDialog(
            onDismiss = { showAddFoodDialog = false },
            onSave = { name, calories, protein, carbs, fat ->
                viewModel.addCustomFood(name, calories, protein, carbs, fat)
                showAddFoodDialog = false
            }
        )
    }

    // Log Portion Dialog
    selectedFoodToLog?.let { food ->
        NutrientPortionDialog(
            title = "Log ${food.name}",
            proteinPer100g = food.protein,
            carbsPer100g = food.carbs,
            fatPer100g = food.fat,
            caloriesPer100g = food.calories,
            onDismiss = { selectedFoodToLog = null },
            onConfirm = { amountGrams ->
                viewModel.logSingleFoodPortion(food, amountGrams)
                selectedFoodToLog = null
            }
        )
    }
}

@Composable
fun FoodListItem(
    food: Food,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NutrientListItem(
        title = food.name,
        protein = food.protein,
        carbs = food.carbs,
        fat = food.fat,
        calories = food.calories,
        trailingSubtitle = "(per 100g)",
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun AddFoodDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, calories: Int, protein: Float, carbs: Float, fat: Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var caloriesStr by remember { mutableStateOf("") }
    var proteinStr by remember { mutableStateOf("") }
    var carbsStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Food") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonalTextField(value = name, onValueChange = { name = it }, label = "Food Name")
                PersonalTextField(value = caloriesStr, onValueChange = { caloriesStr = it }, label = "Calories per 100g")
                PersonalTextField(value = proteinStr, onValueChange = { proteinStr = it }, label = "Protein (g) per 100g")
                PersonalTextField(value = carbsStr, onValueChange = { carbsStr = it }, label = "Carbs (g) per 100g")
                PersonalTextField(value = fatStr, onValueChange = { fatStr = it }, label = "Fat (g) per 100g")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val calories = caloriesStr.toIntOrNull() ?: 0
                    val protein = proteinStr.toFloatOrNull() ?: 0f
                    val carbs = carbsStr.toFloatOrNull() ?: 0f
                    val fat = fatStr.toFloatOrNull() ?: 0f
                    if (name.isNotBlank()) {
                        onSave(name, calories, protein, carbs, fat)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

