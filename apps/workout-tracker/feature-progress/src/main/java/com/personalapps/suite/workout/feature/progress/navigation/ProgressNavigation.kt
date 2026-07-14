package com.personalapps.suite.workout.feature.progress.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.shared.navigation.Destination
import com.personalapps.suite.workout.feature.progress.presentation.ProgressScreen
import com.personalapps.suite.workout.feature.progress.presentation.ProgressViewModel
import com.personalapps.suite.workout.feature.api.navigation.ProgressRoute

fun EntryProviderScope<Destination>.progressEntries(
    viewModel: ProgressViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<ProgressRoute> {
        ProgressScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
