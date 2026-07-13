package com.personalapps.suite.cannabis.feature.stats.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.cannabis.feature.stats.presentation.StatsScreen
import com.personalapps.suite.cannabis.feature.stats.presentation.StatsViewModel
import com.personalapps.suite.shared.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable data object StatsRoute : Destination

fun EntryProviderScope<Destination>.statsEntries(
    viewModel: StatsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<StatsRoute> {
        StatsScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
