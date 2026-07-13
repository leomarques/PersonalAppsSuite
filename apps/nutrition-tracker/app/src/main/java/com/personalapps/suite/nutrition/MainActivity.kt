package com.personalapps.suite.nutrition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.personalapps.suite.nutrition.feature.history.navigation.DashboardRoute
import com.personalapps.suite.nutrition.feature.history.navigation.historyEntries
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryViewModel
import com.personalapps.suite.nutrition.feature.macros.navigation.SetGoalsRoute
import com.personalapps.suite.nutrition.feature.macros.navigation.macroEntries
import com.personalapps.suite.nutrition.feature.macros.presentation.MacroViewModel
import com.personalapps.suite.nutrition.feature.meals.navigation.LogMealRoute
import com.personalapps.suite.nutrition.feature.meals.navigation.mealEntries
import com.personalapps.suite.nutrition.feature.meals.presentation.MealViewModel
import com.personalapps.suite.shared.designsystem.PersonalAppsSuiteTheme
import com.personalapps.suite.shared.navigation.Destination
import com.personalapps.suite.shared.uicomponents.AppScaffold
import com.personalapps.suite.shared.uicomponents.NavItem
import org.koin.androidx.compose.koinViewModel

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

    val historyViewModel: HistoryViewModel = koinViewModel()
    val mealViewModel: MealViewModel = koinViewModel()
    val macroViewModel: MacroViewModel = koinViewModel()

    val navItems = listOf(
        NavItem(
            label = "Dashboard",
            icon = Icons.Default.Home,
            isSelected = backStack.lastOrNull() == DashboardRoute,
            onClick = {
                if (backStack.lastOrNull() != DashboardRoute) {
                    backStack.clear()
                    backStack.add(DashboardRoute)
                }
            }
        ),
        NavItem(
            label = "Add Entry",
            icon = Icons.Default.Edit,
            isSelected = backStack.lastOrNull() == LogMealRoute,
            onClick = {
                if (backStack.lastOrNull() != LogMealRoute) {
                    backStack.clear()
                    backStack.add(LogMealRoute)
                }
            }
        ),
        NavItem(
            label = "Goals",
            icon = Icons.Default.DateRange,
            isSelected = backStack.lastOrNull() == SetGoalsRoute,
            onClick = {
                if (backStack.lastOrNull() != SetGoalsRoute) {
                    backStack.clear()
                    backStack.add(SetGoalsRoute)
                }
            }
        )
    )

    AppScaffold(
        navItems = navItems
    ) { modifier ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                historyEntries(
                    viewModel = historyViewModel,
                    onNavigateToFood = { backStack.add(LogMealRoute) },
                    onNavigateToLogMeal = { backStack.add(LogMealRoute) },
                    onNavigateToConfig = { backStack.add(SetGoalsRoute) },
                    modifier = modifier
                )
                mealEntries(
                    viewModel = mealViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
                macroEntries(
                    viewModel = macroViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
            }
        )
    }
}
