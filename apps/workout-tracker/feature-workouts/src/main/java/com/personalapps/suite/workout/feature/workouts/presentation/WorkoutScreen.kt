package com.personalapps.suite.workout.feature.workouts.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personalapps.suite.shared.common.DateUtils
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.designsystem.PersonalButton
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField
import com.personalapps.suite.workout.feature.exercises.domain.model.Exercise
import com.personalapps.suite.workout.feature.workouts.domain.model.WorkoutSession
import com.personalapps.suite.workout.feature.workouts.domain.model.WorkoutSet

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel,
    onBackClick: () -> Unit,
    onNavigateToExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.sessionsState.collectAsState()
    val sets by viewModel.setsState.collectAsState()
    val exercises by viewModel.exercisesState.collectAsState()

    var showLogDialog by remember { mutableStateOf(false) }

    PersonalScaffold(
        title = "Workout Dashboard",
        onBackClick = onBackClick,
        floatingActionButton = {
            IconButton(
                onClick = { showLogDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                androidx.compose.material3.FloatingActionButton(onClick = { showLogDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Log Workout")
                }
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Workout History",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                PersonalButton(
                    text = "Exercises",
                    onClick = onNavigateToExercises
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (sessions.isEmpty()) {
                EmptyScreen(message = "No logged workouts yet. Start a session using the '+' button below!")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sessions) { session ->
                        val sessionSets = sets.filter { it.workoutSessionId == session.id }
                        WorkoutSessionItemCard(
                            session = session,
                            sessionSets = sessionSets,
                            exercises = exercises,
                            onDelete = { viewModel.deleteSession(session) }
                        )
                    }
                }
            }
        }
    }

    if (showLogDialog) {
        LogWorkoutDialog(
            exercises = exercises,
            onDismiss = { showLogDialog = false },
            onConfirm = { sessionName, setsList ->
                viewModel.createWorkoutSession(sessionName, setsList)
                showLogDialog = false
            }
        )
    }
}

@Composable
fun WorkoutSessionItemCard(
    session: WorkoutSession,
    sessionSets: List<WorkoutSet>,
    exercises: List<Exercise>,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    PersonalCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = DateUtils.formatDateTime(session.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete session",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (sessionSets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                val groupedSets = sessionSets.groupBy { it.exerciseId }
                groupedSets.forEach { (exerciseId, exerciseSets) ->
                    val exerciseName = exercises.find { it.id == exerciseId }?.name ?: "Unknown Exercise"
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            text = exerciseName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        val setsText = exerciseSets.mapIndexed { idx, set ->
                            "Set ${idx + 1}: ${set.loadKg} kg x ${set.reps}"
                        }.joinToString("  •  ")
                        Text(
                            text = setsText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogWorkoutDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onConfirm: (sessionName: String, sets: List<WorkoutSet>) -> Unit
) {
    var sessionName by remember { mutableStateOf("Full Body Workout") }
    val tempSets = remember { mutableStateListOf<WorkoutSet>() }

    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var repsStr by remember { mutableStateOf("10") }
    var loadStr by remember { mutableStateOf("20") }
    var exerciseSearchQuery by remember { mutableStateOf("") }

    LaunchedEffect(exercises) {
        if (selectedExercise == null && exercises.isNotEmpty()) {
            selectedExercise = exercises.first()
        }
    }

    val filteredExercises = remember(exercises, exerciseSearchQuery) {
        if (exerciseSearchQuery.isBlank()) {
            exercises
        } else {
            exercises.filter { it.name.contains(exerciseSearchQuery, ignoreCase = true) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Workout Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonalTextField(
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    label = "Session Name"
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (exercises.isEmpty()) {
                    Text(
                        text = "Create exercises in the library first before starting a session.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Add Exercise Set",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    PersonalTextField(
                        value = exerciseSearchQuery,
                        onValueChange = { exerciseSearchQuery = it },
                        label = "Search exercise...",
                        modifier = Modifier.fillMaxWidth()
                    )

                    val displayExercises = if (exerciseSearchQuery.isEmpty()) exercises else filteredExercises
                    if (displayExercises.isNotEmpty()) {
                        PersonalCard(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 100.dp)
                        ) {
                            LazyColumn {
                                items(displayExercises) { exercise ->
                                    Text(
                                        text = exercise.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedExercise = exercise
                                                exerciseSearchQuery = ""
                                            }
                                            .padding(8.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    selectedExercise?.let { exercise ->
                        Text(
                            text = "Selected: ${exercise.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PersonalTextField(
                                value = repsStr,
                                onValueChange = { repsStr = it },
                                label = "Reps",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            PersonalTextField(
                                value = loadStr,
                                onValueChange = { loadStr = it },
                                label = "Load (kg)",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    val reps = repsStr.toIntOrNull() ?: 10
                                    val load = loadStr.toFloatOrNull() ?: 20f
                                    tempSets.add(
                                        WorkoutSet(
                                            workoutSessionId = 0L,
                                            exerciseId = exercise.id,
                                            reps = reps,
                                            loadKg = load
                                        )
                                    )
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Set")
                            }
                        }
                    }
                }

                if (tempSets.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Session Preview",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    PersonalCard(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 100.dp)
                    ) {
                        LazyColumn(modifier = Modifier.padding(6.dp)) {
                            items(tempSets) { set ->
                                val name = exercises.find { it.id == set.exerciseId }?.name ?: "Exercise"
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$name: ${set.loadKg} kg x ${set.reps}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { tempSets.remove(set) },
                                        modifier = Modifier.width(24.dp).height(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove set",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (sessionName.isNotBlank() && tempSets.isNotEmpty()) {
                        onConfirm(sessionName, tempSets.toList())
                    }
                }
            ) {
                Text("Log Workout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
