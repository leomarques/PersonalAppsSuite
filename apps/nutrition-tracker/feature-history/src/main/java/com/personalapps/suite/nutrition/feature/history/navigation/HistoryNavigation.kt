package com.personalapps.suite.nutrition.feature.history.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryListScreen
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryScreen
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryViewModel
import com.personalapps.suite.shared.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable data object DashboardRoute : Destination
@Serializable data object HistoryListRoute : Destination

fun EntryProviderScope<Destination>.historyEntries(
    viewModel: HistoryViewModel,
    onNavigateToLogMeal: () -> Unit,
    onNavigateToConfig: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<DashboardRoute> {
        HistoryScreen(
            viewModel = viewModel,
            onNavigateToLogMeal = onNavigateToLogMeal,
            onNavigateToConfig = onNavigateToConfig,
            onNavigateToHistory = onNavigateToHistory,
            modifier = modifier
        )
    }
    entry<HistoryListRoute> {
        HistoryListScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
