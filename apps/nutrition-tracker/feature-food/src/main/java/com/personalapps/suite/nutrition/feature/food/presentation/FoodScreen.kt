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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personalapps.suite.nutrition.feature.api.model.Food
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.designsystem.PersonalButton
import com.personalapps.suite.shared.uicomponents.NutrientListItem
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField
import com.personalapps.suite.shared.uicomponents.SwipeToDeleteContainer

@Composable
fun FoodScreen(
    viewModel: FoodViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FoodEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is FoodEffect.FoodAdded -> snackbarHostState.showSnackbar("Food added")
                is FoodEffect.FoodDeleted -> snackbarHostState.showSnackbar("Food deleted")
            }
        }
    }

    var showAddForm by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var caloriesStr by remember { mutableStateOf("") }
    var proteinStr by remember { mutableStateOf("") }
    var carbsStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }

    PersonalScaffold(
        title = "Food Database",
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
            if (showAddForm) {
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            text = "Add New Food",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        PersonalTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Food Name",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            PersonalTextField(
                                value = caloriesStr,
                                onValueChange = { caloriesStr = it },
                                label = "Calories (kcal)",
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            )
                            PersonalTextField(
                                value = proteinStr,
                                onValueChange = { proteinStr = it },
                                label = "Protein (g)",
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            PersonalTextField(
                                value = carbsStr,
                                onValueChange = { carbsStr = it },
                                label = "Carbs (g)",
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            )
                            PersonalTextField(
                                value = fatStr,
                                onValueChange = { fatStr = it },
                                label = "Fat (g)",
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { showAddForm = false }) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            PersonalButton(
                                text = "Save",
                                onClick = {
                                    val calories = caloriesStr.toIntOrNull() ?: 0
                                    val protein = proteinStr.toFloatOrNull() ?: 0f
                                    val carbs = carbsStr.toFloatOrNull() ?: 0f
                                    val fat = fatStr.toFloatOrNull() ?: 0f
                                    if (name.isNotBlank()) {
                                        viewModel.addFood(name, calories, protein, carbs, fat)
                                        name = ""
                                        caloriesStr = ""
                                        proteinStr = ""
                                        carbsStr = ""
                                        fatStr = ""
                                        showAddForm = false
                                    }
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                PersonalButton(
                    text = "Add Custom Food",
                    onClick = { showAddForm = true },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.foods.isEmpty()) {
                EmptyScreen(message = "Tap 'Add Custom Food' to build your local nutrition database.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = state.foods,
                        key = { it.name }
                    ) { food ->
                        SwipeToDeleteContainer(
                            onDelete = { viewModel.deleteFood(food) },
                            confirmTitle = "Delete Food",
                            confirmMessage = "Are you sure you want to delete '${food.name}' from the database?"
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
        leadingSubtitle = "${food.calories} kcal",
        modifier = modifier
    )
}
