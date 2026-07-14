package com.personalapps.suite.nutrition.feature.food.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.nutrition.feature.food.presentation.FoodScreen
import com.personalapps.suite.nutrition.feature.food.presentation.FoodViewModel
import com.personalapps.suite.shared.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable data object FoodDatabaseRoute : Destination

fun EntryProviderScope<Destination>.foodEntries(
    viewModel: FoodViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<FoodDatabaseRoute> {
        FoodScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
