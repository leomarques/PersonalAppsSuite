package com.personalapps.suite.nutrition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.personalapps.suite.nutrition.feature.food.navigation.foodEntries
import com.personalapps.suite.nutrition.feature.history.navigation.DashboardRoute
import com.personalapps.suite.nutrition.feature.history.navigation.HistoryListRoute
import com.personalapps.suite.nutrition.feature.history.navigation.historyEntries
import com.personalapps.suite.nutrition.feature.macros.navigation.SetGoalsRoute
import com.personalapps.suite.nutrition.feature.macros.navigation.macroEntries
import com.personalapps.suite.nutrition.feature.meals.navigation.LogMealRoute
import com.personalapps.suite.nutrition.feature.meals.navigation.mealEntries
import com.personalapps.suite.shared.designsystem.PersonalAppsSuiteTheme
import com.personalapps.suite.shared.navigation.Destination
import com.personalapps.suite.shared.uicomponents.AppScaffold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalAppsSuiteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(DashboardRoute) as NavBackStack<Destination>

    AppScaffold(
        navItems = emptyList()
    ) { modifier ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                historyEntries(
                    onNavigateToLogMeal = { backStack.add(LogMealRoute) },
                    onNavigateToConfig = { backStack.add(SetGoalsRoute) },
                    onNavigateToHistory = { backStack.add(HistoryListRoute) },
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
                mealEntries(
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
                macroEntries(
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
                foodEntries(
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
            }
        )
    }
}
