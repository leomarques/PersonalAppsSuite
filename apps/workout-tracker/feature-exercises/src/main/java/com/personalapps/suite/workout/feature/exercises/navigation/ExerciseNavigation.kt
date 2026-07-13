package com.personalapps.suite.workout.feature.exercises.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.shared.navigation.Destination
import com.personalapps.suite.workout.feature.exercises.presentation.ExerciseScreen
import com.personalapps.suite.workout.feature.exercises.presentation.ExerciseViewModel
import kotlinx.serialization.Serializable

@Serializable data object ExercisesRoute : Destination

fun EntryProviderScope<Destination>.exerciseEntries(
    viewModel: ExerciseViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<ExercisesRoute> {
        ExerciseScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
