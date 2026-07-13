package com.personalapps.suite.nutrition.feature.macros.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.nutrition.feature.macros.presentation.MacroScreen
import com.personalapps.suite.nutrition.feature.macros.presentation.MacroViewModel
import com.personalapps.suite.shared.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable data object SetGoalsRoute : Destination

fun EntryProviderScope<Destination>.macroEntries(
    viewModel: MacroViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<SetGoalsRoute> {
        MacroScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
