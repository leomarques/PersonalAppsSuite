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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.personalapps.suite.nutrition.feature.api.model.Food
import kotlinx.coroutines.launch
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.uicomponents.NutrientListItem
import com.personalapps.suite.shared.uicomponents.NutrientPortionDialog
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField
import com.personalapps.suite.shared.uicomponents.SwipeActionContainer

@Composable
fun MealScreen(
    viewModel: MealViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var showAddFoodDialog by remember { mutableStateOf(false) }
    var editingFood by remember { mutableStateOf<Food?>(null) }
    var selectedFoodToLog by remember { mutableStateOf<Food?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MealEffect.ShowError -> scope.launch { snackbarHostState.showSnackbar(effect.message) }
                is MealEffect.MealLogged -> scope.launch { snackbarHostState.showSnackbar("Meal logged successfully") }
                is MealEffect.MealDeleted -> scope.launch { snackbarHostState.showSnackbar("Meal deleted") }
                is MealEffect.FoodAdded -> {
                    scope.launch { snackbarHostState.showSnackbar("Custom food added to database") }
                    selectedFoodToLog = effect.food
                }
                is MealEffect.FoodUpdated -> scope.launch { snackbarHostState.showSnackbar("Food updated") }
                is MealEffect.FoodDeleted -> scope.launch { snackbarHostState.showSnackbar("Food deleted from library") }
            }
        }
    }

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
                    items(items = filteredFoods, key = { it.id }) { food ->
                        SwipeActionContainer(
                            onDelete = { viewModel.deleteFood(food) },
                            onEdit = { editingFood = food },
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

    // Add/Edit Custom Food Dialog
    if (showAddFoodDialog || editingFood != null) {
        val foodToEdit = editingFood
        AddFoodDialog(
            initialFood = foodToEdit,
            onDismiss = { 
                showAddFoodDialog = false
                editingFood = null
            },
            onSave = { name, calories, protein, carbs, fat, gramsPerServing ->
                if (foodToEdit != null) {
                    viewModel.updateFood(foodToEdit.id, name, calories, protein, carbs, fat, gramsPerServing)
                } else {
                    viewModel.addCustomFood(name, calories, protein, carbs, fat, gramsPerServing)
                }
                showAddFoodDialog = false
                editingFood = null
            }
        )
    }

    // Log Portion Dialog
    selectedFoodToLog?.let { food ->
        NutrientPortionDialog(
            title = "Log ${food.name}",
            proteinPerServing = food.protein,
            carbsPerServing = food.carbs,
            fatPerServing = food.fat,
            caloriesPerServing = food.calories,
            gramsPerServing = food.gramsPerServing,
            initialAmountGrams = food.gramsPerServing,
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
        trailingSubtitle = "(per ${food.gramsPerServing.toInt()}g)",
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun AddFoodDialog(
    initialFood: Food? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, calories: Int, protein: Float, carbs: Float, fat: Float, gramsPerServing: Float) -> Unit
) {
    var name by remember { mutableStateOf(initialFood?.name ?: "") }
    var caloriesStr by remember { mutableStateOf(initialFood?.calories?.toString() ?: "") }
    var proteinStr by remember { mutableStateOf(initialFood?.protein?.toString() ?: "") }
    var carbsStr by remember { mutableStateOf(initialFood?.carbs?.toString() ?: "") }
    var fatStr by remember { mutableStateOf(initialFood?.fat?.toString() ?: "") }
    var gramsPerServingStr by remember { mutableStateOf(initialFood?.gramsPerServing?.toString() ?: "100") }
    
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialFood != null) "Edit Food" else "Add Custom Food") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonalTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Food Name",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    focusRequester = focusRequester
                )
                PersonalTextField(
                    value = caloriesStr,
                    onValueChange = { caloriesStr = it },
                    label = "Calories per Serving",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                PersonalTextField(
                    value = proteinStr,
                    onValueChange = { proteinStr = it },
                    label = "Protein (g) per Serving",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                PersonalTextField(
                    value = carbsStr,
                    onValueChange = { carbsStr = it },
                    label = "Carbs (g) per Serving",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                PersonalTextField(
                    value = fatStr,
                    onValueChange = { fatStr = it },
                    label = "Fat (g) per Serving",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                PersonalTextField(
                    value = gramsPerServingStr,
                    onValueChange = { gramsPerServingStr = it },
                    label = "Grams per Serving (e.g., 100)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val calories = caloriesStr.toIntOrNull() ?: 0
                    val protein = proteinStr.toFloatOrNull() ?: 0f
                    val carbs = carbsStr.toFloatOrNull() ?: 0f
                    val fat = fatStr.toFloatOrNull() ?: 0f
                    val gramsPerServing = gramsPerServingStr.toFloatOrNull() ?: 100f
                    if (name.isNotBlank()) {
                        onSave(name, calories, protein, carbs, fat, gramsPerServing)
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

