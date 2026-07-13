package com.personalapps.suite.nutrition.feature.history.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryScreen
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryViewModel
import com.personalapps.suite.shared.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable data object DashboardRoute : Destination

fun EntryProviderScope<Destination>.historyEntries(
    viewModel: HistoryViewModel,
    onNavigateToFood: () -> Unit,
    onNavigateToLogMeal: () -> Unit,
    onNavigateToConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<DashboardRoute> {
        HistoryScreen(
            viewModel = viewModel,
            onNavigateToFood = onNavigateToFood,
            onNavigateToLogMeal = onNavigateToLogMeal,
            onNavigateToConfig = onNavigateToConfig,
            modifier = modifier
        )
    }
}
