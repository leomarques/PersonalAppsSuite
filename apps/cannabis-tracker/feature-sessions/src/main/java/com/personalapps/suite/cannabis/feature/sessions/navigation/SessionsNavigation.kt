package com.personalapps.suite.cannabis.feature.sessions.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.cannabis.feature.sessions.presentation.SessionsScreen
import com.personalapps.suite.cannabis.feature.sessions.presentation.SessionsViewModel
import com.personalapps.suite.shared.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable data object SessionsRoute : Destination

fun EntryProviderScope<Destination>.sessionsEntries(
    viewModel: SessionsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<SessionsRoute> {
        SessionsScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
