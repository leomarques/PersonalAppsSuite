package com.personalapps.suite.workout.feature.workouts.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.shared.navigation.Destination
import com.personalapps.suite.workout.feature.workouts.presentation.WorkoutScreen
import com.personalapps.suite.workout.feature.workouts.presentation.WorkoutViewModel
import com.personalapps.suite.workout.feature.api.navigation.DashboardRoute

fun EntryProviderScope<Destination>.workoutEntries(
    viewModel: WorkoutViewModel,
    onBackClick: () -> Unit,
    onNavigateToExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<DashboardRoute> {
        WorkoutScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onNavigateToExercises = onNavigateToExercises,
            modifier = modifier
        )
    }
}
