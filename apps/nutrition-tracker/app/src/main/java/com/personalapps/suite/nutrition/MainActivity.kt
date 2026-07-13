package com.personalapps.suite.nutrition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = backStack.lastOrNull() == DashboardRoute,
                    onClick = {
                        if (backStack.lastOrNull() != DashboardRoute) {
                            backStack.clear()
                            backStack.add(DashboardRoute)
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = backStack.lastOrNull() == LogMealRoute,
                    onClick = {
                        if (backStack.lastOrNull() != LogMealRoute) {
                            backStack.clear()
                            backStack.add(LogMealRoute)
                        }
                    },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Add Entry") },
                    label = { Text("Add Entry") }
                )
                NavigationBarItem(
                    selected = backStack.lastOrNull() == SetGoalsRoute,
                    onClick = {
                        if (backStack.lastOrNull() != SetGoalsRoute) {
                            backStack.clear()
                            backStack.add(SetGoalsRoute)
                        }
                    },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Goals") },
                    label = { Text("Goals") }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                historyEntries(
                    viewModel = historyViewModel,
                    onNavigateToFood = { backStack.add(LogMealRoute) },
                    onNavigateToLogMeal = { backStack.add(LogMealRoute) },
                    onNavigateToConfig = { backStack.add(SetGoalsRoute) },
                    modifier = Modifier.padding(innerPadding)
                )
                mealEntries(
                    viewModel = mealViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = Modifier.padding(innerPadding)
                )
                macroEntries(
                    viewModel = macroViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        )
    }
}
