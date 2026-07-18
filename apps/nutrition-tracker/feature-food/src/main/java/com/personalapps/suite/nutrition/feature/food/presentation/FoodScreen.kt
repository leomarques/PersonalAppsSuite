package com.personalapps.suite.nutrition.feature.food.presentation

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
import androidx.compose.ui.res.stringResource
import com.personalapps.suite.nutrition.feature.food.R
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.designsystem.PersonalButton
import com.personalapps.suite.shared.uicomponents.NutrientListItem
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField
import com.personalapps.suite.shared.uicomponents.SwipeActionContainer

@Composable
fun FoodScreen(
    viewModel: FoodViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val addFoodSuccess = stringResource(R.string.add_food_success)
    val updateFoodSuccess = stringResource(R.string.update_food_success)
    val deleteFoodSuccess = stringResource(R.string.delete_food_success)

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FoodEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is FoodEffect.FoodAdded -> snackbarHostState.showSnackbar(addFoodSuccess)
                is FoodEffect.FoodUpdated -> snackbarHostState.showSnackbar(updateFoodSuccess)
                is FoodEffect.FoodDeleted -> snackbarHostState.showSnackbar(deleteFoodSuccess)
            }
        }
    }

    var showAddForm by remember { mutableStateOf(false) }
    var editingFood by remember { mutableStateOf<Food?>(null) }
    val focusRequester = remember { FocusRequester() }

    var name by remember { mutableStateOf("") }
    var caloriesStr by remember { mutableStateOf("") }
    var proteinStr by remember { mutableStateOf("") }
    var carbsStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }

    PersonalScaffold(
        title = stringResource(R.string.food_database),
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
            if (showAddForm || editingFood != null) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            text = if (editingFood != null) stringResource(R.string.edit_food) else stringResource(R.string.add_new_food),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        PersonalTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = stringResource(R.string.food_name),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            focusRequester = focusRequester,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            PersonalTextField(
                                value = caloriesStr,
                                onValueChange = { caloriesStr = it },
                                label = stringResource(R.string.calories_kcal_label),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            )
                            PersonalTextField(
                                value = proteinStr,
                                onValueChange = { proteinStr = it },
                                label = stringResource(R.string.protein_g),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            PersonalTextField(
                                value = carbsStr,
                                onValueChange = { carbsStr = it },
                                label = stringResource(R.string.carbs_g),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            )
                            PersonalTextField(
                                value = fatStr,
                                onValueChange = { fatStr = it },
                                label = stringResource(R.string.fat_g),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = {
                                showAddForm = false
                                editingFood = null
                                name = ""
                                caloriesStr = ""
                                proteinStr = ""
                                carbsStr = ""
                                fatStr = ""
                            }) {
                                Text(stringResource(R.string.cancel))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            PersonalButton(
                                text = stringResource(R.string.save),
                                onClick = {
                                    editingFood?.let {
                                        viewModel.updateFood(it.id, name, caloriesStr, proteinStr, carbsStr, fatStr)
                                    } ?: run {
                                        viewModel.addFood(name, caloriesStr, proteinStr, carbsStr, fatStr)
                                    }
                                    name = ""
                                    caloriesStr = ""
                                    proteinStr = ""
                                    carbsStr = ""
                                    fatStr = ""
                                    showAddForm = false
                                    editingFood = null
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                PersonalButton(
                    text = stringResource(R.string.add_custom_food),
                    onClick = { showAddForm = true },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.foods.isEmpty()) {
                EmptyScreen(message = stringResource(R.string.empty_food_db_message))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = state.foods,
                        key = { it.id }
                    ) { food ->
                        SwipeActionContainer(
                            onDelete = { viewModel.deleteFood(food) },
                            onEdit = {
                                editingFood = food
                                name = food.name
                                caloriesStr = food.calories.toString()
                                proteinStr = food.protein.toString()
                                carbsStr = food.carbs.toString()
                                fatStr = food.fat.toString()
                            },
                            confirmTitle = stringResource(R.string.delete_food_confirm_title),
                            confirmMessage = stringResource(R.string.delete_food_confirm_message, food.name)
                        ) {
                            FoodListItem(food = food)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodListItem(
    food: Food,
    modifier: Modifier = Modifier
) {
    NutrientListItem(
        title = food.name,
        protein = food.protein,
        carbs = food.carbs,
        fat = food.fat,
        leadingSubtitle = stringResource(com.personalapps.suite.shared.uicomponents.R.string.calories_kcal, food.calories),
        modifier = modifier
    )
}
