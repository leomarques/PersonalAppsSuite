package com.personalapps.suite.nutrition.feature.meals.presentation

import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.personalapps.suite.nutrition.feature.meals.R
import com.personalapps.suite.nutrition.feature.api.model.Food
import kotlinx.coroutines.launch
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.uicomponents.NutrientListItem
import com.personalapps.suite.shared.uicomponents.NutrientPortionDialog
import com.personalapps.suite.shared.uicomponents.NutrientRow
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
    var showMixDialog by remember { mutableStateOf(false) }
    var editingFood by remember { mutableStateOf<Food?>(null) }
    var selectedFoodToLog by remember { mutableStateOf<Food?>(null) }

    val mealLoggedSuccess = stringResource(R.string.meal_logged_success)
    val mealDeleted = stringResource(R.string.meal_deleted_success)
    val customFoodAdded = stringResource(R.string.custom_food_added_success)
    val foodUpdated = stringResource(R.string.food_updated_success)
    val foodDeletedFromLibrary = stringResource(R.string.food_deleted_library_success)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelection()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MealEffect.ShowError -> scope.launch { snackbarHostState.showSnackbar(effect.message) }
                is MealEffect.MealLogged -> scope.launch { snackbarHostState.showSnackbar(mealLoggedSuccess) }
                is MealEffect.MealDeleted -> scope.launch { snackbarHostState.showSnackbar(mealDeleted) }
                is MealEffect.FoodAdded -> {
                    scope.launch { snackbarHostState.showSnackbar(customFoodAdded) }
                    selectedFoodToLog = effect.food
                }
                is MealEffect.FoodUpdated -> scope.launch { snackbarHostState.showSnackbar(foodUpdated) }
                is MealEffect.FoodDeleted -> scope.launch { snackbarHostState.showSnackbar(foodDeletedFromLibrary) }
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
        title = if (state.selectedFoodIds.isEmpty()) {
            stringResource(R.string.add_entry_title)
        } else {
            stringResource(R.string.items_selected, state.selectedFoodIds.size)
        },
        onBackClick = onBackClick,
        actions = {
            if (state.selectedFoodIds.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearSelection() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = stringResource(R.string.clear_selection))
                }
                IconButton(onClick = { showMixDialog = true }) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = stringResource(R.string.mix_and_log))
                }
            } else {
                IconButton(onClick = { viewModel.setMultiSelectMode(!state.isMultiSelectMode) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.List,
                        contentDescription = stringResource(R.string.toggle_multi_select),
                        tint = if (state.isMultiSelectMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { showAddFoodDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.new_food)
                    )
                }
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
                label = stringResource(R.string.search_foods),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    }
                } else null,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.food_library_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredFoods.isEmpty()) {
                val msg = if (searchQuery.isBlank()) {
                    stringResource(R.string.empty_food_db_long_message)
                } else {
                    stringResource(R.string.no_matching_foods)
                }
                EmptyScreen(message = msg)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items = filteredFoods, key = { it.id }) { food ->
                        val isSelected = state.selectedFoodIds.contains(food.id)
                        SwipeActionContainer(
                            onDelete = { viewModel.deleteFood(food) },
                            onEdit = { editingFood = food },
                            confirmTitle = stringResource(R.string.delete_food),
                            confirmMessage = stringResource(R.string.delete_food_library_confirm_message, food.name)
                        ) {
                            FoodListItem(
                                food = food,
                                isSelected = isSelected,
                                showCheckbox = state.isMultiSelectMode,
                                onSelectToggle = { viewModel.toggleFoodSelection(food.id) },
                                onClick = {
                                    if (state.isMultiSelectMode) {
                                        viewModel.toggleFoodSelection(food.id)
                                    } else {
                                        selectedFoodToLog = food
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMixDialog) {
        val selectedFoods = remember(state.selectedFoodIds, state.foods) {
            state.foods.filter { state.selectedFoodIds.contains(it.id) }
        }
        MixFoodsDialog(
            foods = selectedFoods,
            onDismiss = { showMixDialog = false },
            onConfirm = { name, foodPortions ->
                viewModel.logMixedMeal(name, foodPortions)
                showMixDialog = false
            }
        )
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
                    viewModel.updateFood(
                        foodToEdit.copy(
                            name = name,
                            calories = calories,
                            protein = protein,
                            carbs = carbs,
                            fat = fat,
                            gramsPerServing = gramsPerServing
                        )
                    )
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
            title = stringResource(R.string.log_food_title, food.name),
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
    isSelected: Boolean,
    showCheckbox: Boolean,
    onSelectToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NutrientListItem(
        title = food.name,
        protein = food.protein,
        carbs = food.carbs,
        fat = food.fat,
        calories = food.calories,
        trailingSubtitle = if (!showCheckbox) stringResource(com.personalapps.suite.shared.uicomponents.R.string.per_grams_label, food.gramsPerServing.toInt()) else null,
        onClick = onClick,
        leadingContent = if (showCheckbox) {
            {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onSelectToggle() }
                )
            }
        } else null,
        modifier = modifier
    )
}

@Composable
fun MixFoodsDialog(
    foods: List<Food>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, portions: List<Pair<Food, Float>>) -> Unit
) {
    var mealName by remember { mutableStateOf("") }
    val amounts = remember { mutableStateMapOf<Long, String>().apply {
        foods.forEach { put(it.id, it.gramsPerServing.toInt().toString()) }
    } }

    val totalNutrients by remember {
        derivedStateOf {
            var totalCal = 0
            var totalP = 0f
            var totalC = 0f
            var totalF = 0f
            foods.forEach { food ->
                val amount = amounts[food.id]?.toFloatOrNull() ?: 0f
                val factor = amount / food.gramsPerServing
                totalCal += (food.calories * factor).toInt()
                totalP += food.protein * factor
                totalC += food.carbs * factor
                totalF += food.fat * factor
            }
            Triple(totalCal, totalP, Pair(totalC, totalF))
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.mix_selected_items)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PersonalTextField(
                    value = mealName,
                    onValueChange = { mealName = it },
                    label = stringResource(R.string.meal_name),
                    modifier = Modifier.fillMaxWidth()
                )
                
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = foods, key = { it.id }) { food ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(food.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            PersonalTextField(
                                value = amounts[food.id] ?: "",
                                onValueChange = { amounts[food.id] = it },
                                label = stringResource(R.string.grams),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text(stringResource(R.string.total_mix_nutrients), style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = stringResource(com.personalapps.suite.shared.uicomponents.R.string.calories_kcal, totalNutrients.first),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    NutrientRow(
                        protein = totalNutrients.second,
                        carbs = totalNutrients.third.first,
                        fat = totalNutrients.third.second
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (mealName.isNotBlank()) {
                        val portions = foods.map { food ->
                            food to (amounts[food.id]?.toFloatOrNull() ?: 0f)
                        }
                        onConfirm(mealName, portions)
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialFood != null) stringResource(R.string.edit_food) else stringResource(R.string.add_custom_food)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonalTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.food_name),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                PersonalTextField(
                    value = caloriesStr,
                    onValueChange = { caloriesStr = it },
                    label = stringResource(R.string.calories_per_serving),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                PersonalTextField(
                    value = proteinStr,
                    onValueChange = { proteinStr = it },
                    label = stringResource(R.string.protein_g_per_serving),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                PersonalTextField(
                    value = carbsStr,
                    onValueChange = { carbsStr = it },
                    label = stringResource(R.string.carbs_g_per_serving),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                PersonalTextField(
                    value = fatStr,
                    onValueChange = { fatStr = it },
                    label = stringResource(R.string.fat_g_per_serving),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                PersonalTextField(
                    value = gramsPerServingStr,
                    onValueChange = { gramsPerServingStr = it },
                    label = stringResource(R.string.grams_per_serving_label),
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
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

