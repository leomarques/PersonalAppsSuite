package com.personalapps.suite.nutrition.feature.meals.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.personalapps.suite.nutrition.feature.meals.presentation.MealScreen
import com.personalapps.suite.nutrition.feature.meals.presentation.MealViewModel
import com.personalapps.suite.shared.navigation.Destination
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable data object LogMealRoute : Destination

fun EntryProviderScope<Destination>.mealEntries(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    entry<LogMealRoute> {
        val viewModel: MealViewModel = koinViewModel()
        MealScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
