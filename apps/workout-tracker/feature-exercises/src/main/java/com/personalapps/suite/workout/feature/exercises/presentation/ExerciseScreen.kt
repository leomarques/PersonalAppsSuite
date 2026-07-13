package com.personalapps.suite.workout.feature.exercises.presentation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.designsystem.PersonalButton
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.PersonalDropdownMenu
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField
import com.personalapps.suite.workout.feature.api.model.Exercise

@Composable
fun ExerciseScreen(
    viewModel: ExerciseViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExerciseEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is ExerciseEffect.ExerciseAdded -> snackbarHostState.showSnackbar("Exercise added successfully")
                is ExerciseEffect.ExerciseDeleted -> snackbarHostState.showSnackbar("Exercise deleted")
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredExercises = remember(state.exercises, searchQuery) {
        if (searchQuery.isBlank()) {
            state.exercises
        } else {
            state.exercises.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    PersonalScaffold(
        title = "Exercises Library",
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
                    label = "Search exercises...",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                PersonalButton(
                    text = "New Exercise",
                    onClick = { showAddDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "My Exercises",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredExercises.isEmpty()) {
                val msg = if (searchQuery.isBlank()) {
                    "Exercises database is empty. Create a custom exercise to begin logging workouts!"
                } else {
                    "No exercises matching '$searchQuery'."
                }
                EmptyScreen(message = msg)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredExercises) { exercise ->
                        ExerciseItemCard(
                            exercise = exercise,
                            onDelete = { viewModel.deleteExercise(exercise) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExerciseDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, category ->
                viewModel.addExercise(name, category)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ExerciseItemCard(
    exercise: Exercise,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    PersonalCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = exercise.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete exercise",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, category: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Chest") }
    val categories = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Exercise") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PersonalTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Exercise Name"
                )
                PersonalDropdownMenu(
                    options = categories,
                    selectedOption = category,
                    onOptionSelected = { category = it },
                    label = "Category"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, category)
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
