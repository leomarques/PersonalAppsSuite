package com.personalapps.suite.cannabis.feature.history.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.cannabis.feature.history.presentation.HistoryScreen
import com.personalapps.suite.cannabis.feature.history.presentation.HistoryViewModel
import com.personalapps.suite.shared.navigation.Destination
import com.personalapps.suite.cannabis.feature.api.navigation.HistoryRoute

fun EntryProviderScope<Destination>.historyEntries(
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<HistoryRoute> {
        HistoryScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
