package com.personalapps.suite.nutrition.feature.history.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryListScreen
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryScreen
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryViewModel
import com.personalapps.suite.shared.navigation.Destination
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable data object DashboardRoute : Destination
@Serializable data object HistoryListRoute : Destination

fun EntryProviderScope<Destination>.historyEntries(
    onNavigateToLogMeal: () -> Unit,
    onNavigateToConfig: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<DashboardRoute> {
        val viewModel: HistoryViewModel = koinViewModel()
        HistoryScreen(
            viewModel = viewModel,
            onNavigateToLogMeal = onNavigateToLogMeal,
            onNavigateToConfig = onNavigateToConfig,
            onNavigateToHistory = onNavigateToHistory,
            modifier = modifier
        )
    }
    entry<HistoryListRoute> {
        val viewModel: HistoryViewModel = koinViewModel()
        HistoryListScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
