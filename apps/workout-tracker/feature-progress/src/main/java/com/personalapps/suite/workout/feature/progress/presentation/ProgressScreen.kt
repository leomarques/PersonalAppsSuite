package com.personalapps.suite.workout.feature.progress.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.PersonalDropdownMenu
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.workout.feature.api.model.Exercise
import com.personalapps.suite.workout.feature.progress.domain.usecase.ExerciseProgressPoint
import com.personalapps.suite.workout.feature.progress.domain.usecase.GetProgressPointsUseCase


@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProgressEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var selectedMetricIndex by remember { mutableStateOf(0) } // 0 = Max Load, 1 = Volume

    LaunchedEffect(state.exercises) {
        if (selectedExercise == null && state.exercises.isNotEmpty()) {
            selectedExercise = state.exercises.first()
        }
    }

    val progressPoints = remember(selectedExercise, state.sessions) {
        selectedExercise?.let { exercise ->
            viewModel.getProgressPoints(exercise.id, state.sessions)
        } ?: emptyList()
    }

    PersonalScaffold(
        title = "Progress Dashboard",
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
            if (state.exercises.isEmpty()) {
                EmptyScreen(message = "Create exercises and log sessions first to view performance evolution!")
            } else {
                Text(
                    text = "Track Exercise Performance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                selectedExercise?.let { exercise ->
                    PersonalDropdownMenu(
                        options = state.exercises,
                        selectedOption = exercise,
                        onOptionSelected = { selectedExercise = it },
                        label = "Select Exercise",
                        optionToString = { it.name }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedMetricIndex == 0,
                        onClick = { selectedMetricIndex = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("Max Load")
                    }
                    SegmentedButton(
                        selected = selectedMetricIndex == 1,
                        onClick = { selectedMetricIndex = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("Total Volume")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (progressPoints.size < 2) {
                    EmptyScreen(message = "Log at least 2 workouts for this exercise to generate a progression chart.")
                } else {
                    PersonalCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            val metricLabel = if (selectedMetricIndex == 0) "Max Load (kg)" else "Total Volume (kg)"
                            Text(
                                text = "Progression: $metricLabel",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            PerformanceChart(
                                points = progressPoints,
                                useVolume = selectedMetricIndex == 1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                lineColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceChart(
    points: List<ExerciseProgressPoint>,
    useVolume: Boolean,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue
) {
    val values = points.map { if (useVolume) it.totalVolume else it.maxLoadKg }
    val minY = remember(values) { (values.minOrNull() ?: 0f) * 0.9f }
    val maxY = remember(values) { (values.maxOrNull() ?: 100f) * 1.1f }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 40.dp.toPx()
        val chartWidth = width - (padding * 2)
        val chartHeight = height - (padding * 2)

        if (points.isEmpty()) return@Canvas

        val xSteps = points.size - 1
        val yDelta = if (maxY - minY == 0f) 1f else (maxY - minY)

        val coords = points.mapIndexed { idx, _ ->
            val valY = values[idx]
            val x = padding + (idx.toFloat() / xSteps.toFloat()) * chartWidth
            val y = padding + chartHeight - ((valY - minY) / yDelta) * chartHeight
            Offset(x, y)
        }

        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(padding, padding),
            end = Offset(padding, padding + chartHeight),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(padding, padding + chartHeight),
            end = Offset(padding + chartWidth, padding + chartHeight),
            strokeWidth = 2f
        )

        val gridLines = 3
        for (i in 0..gridLines) {
            val ratio = i.toFloat() / gridLines.toFloat()
            val y = padding + ratio * chartHeight
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(padding, y),
                end = Offset(padding + chartWidth, y),
                strokeWidth = 1f
            )
        }

        val path = Path().apply {
            moveTo(coords.first().x, coords.first().y)
            for (i in 1 until coords.size) {
                lineTo(coords[i].x, coords[i].y)
            }
        }

        val fillPath = Path().apply {
            addPath(path)
            lineTo(coords.last().x, padding + chartHeight)
            lineTo(coords.first().x, padding + chartHeight)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent),
                startY = coords.minOf { it.y },
                endY = padding + chartHeight
            )
        )

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )

        coords.forEach { offset ->
            drawCircle(
                color = lineColor,
                radius = 5.dp.toPx(),
                center = offset
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = offset
            )
        }
    }
}
