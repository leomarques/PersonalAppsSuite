package com.personalapps.suite.nutrition.feature.meals.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.designsystem.PersonalButton
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.PersonalDropdownMenu
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField

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
        snackbarHostState = snackbarHostState,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                PersonalTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search foods...",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                PersonalButton(
                    text = "New Food",
                    onClick = { showAddFoodDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Food Database Library",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredFoods.isEmpty()) {
                val msg = if (searchQuery.isBlank()) {
                    "Your food database is empty. Create a custom food item using the 'New Food' button to start logging!"
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
                        FoodListItem(
                            food = food,
                            onClick = { selectedFoodToLog = food }
                        )
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
        LogPortionDialog(
            food = food,
            onDismiss = { selectedFoodToLog = null },
            onConfirm = { amountGrams, mealType ->
                viewModel.logSingleFoodPortion(food, amountGrams, mealType)
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
    PersonalCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${food.calories} kcal  •  P: ${food.protein}g  •  C: ${food.carbs}g  •  F: ${food.fat}g (per 100g)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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

@Composable
fun LogPortionDialog(
    food: Food,
    onDismiss: () -> Unit,
    onConfirm: (amountGrams: Float, mealType: String) -> Unit
) {
    var amountStr by remember { mutableStateOf("100") }
    var mealType by remember { mutableStateOf("Breakfast") }
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log ${food.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Nutritional values per 100g:\n${food.calories} kcal  •  Protein: ${food.protein}g  •  Carbs: ${food.carbs}g  •  Fat: ${food.fat}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PersonalDropdownMenu(
                    options = mealTypes,
                    selectedOption = mealType,
                    onOptionSelected = { mealType = it },
                    label = "Meal Type"
                )
                PersonalTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = "Amount (grams)"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountStr.toFloatOrNull() ?: 100f
                    if (amount > 0f) {
                        onConfirm(amount, mealType)
                    }
                }
            ) {
                Text("Add to Day")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
