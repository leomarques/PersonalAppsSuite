package com.personalapps.suite.cannabis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
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
import com.personalapps.suite.shared.designsystem.PersonalAppsSuiteTheme
import com.personalapps.suite.shared.navigation.Destination
import com.personalapps.suite.shared.uicomponents.AppScaffold
import com.personalapps.suite.shared.uicomponents.NavItem
import com.personalapps.suite.cannabis.feature.history.navigation.HistoryRoute
import com.personalapps.suite.cannabis.feature.history.navigation.historyEntries
import com.personalapps.suite.cannabis.feature.history.presentation.HistoryViewModel
import com.personalapps.suite.cannabis.feature.sessions.navigation.SessionsRoute
import com.personalapps.suite.cannabis.feature.sessions.navigation.sessionsEntries
import com.personalapps.suite.cannabis.feature.sessions.presentation.SessionsViewModel
import com.personalapps.suite.cannabis.feature.stats.navigation.StatsRoute
import com.personalapps.suite.cannabis.feature.stats.navigation.statsEntries
import com.personalapps.suite.cannabis.feature.stats.presentation.StatsViewModel
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
    val backStack = rememberNavBackStack(SessionsRoute) as NavBackStack<Destination>

    val sessionsViewModel: SessionsViewModel = koinViewModel()
    val historyViewModel: HistoryViewModel = koinViewModel()
    val statsViewModel: StatsViewModel = koinViewModel()

    val navItems = listOf(
        NavItem(
            label = "Sessions",
            icon = Icons.Default.Home,
            isSelected = backStack.lastOrNull() == SessionsRoute,
            onClick = {
                if (backStack.lastOrNull() != SessionsRoute) {
                    backStack.clear()
                    backStack.add(SessionsRoute)
                }
            }
        ),
        NavItem(
            label = "History",
            icon = Icons.Default.List,
            isSelected = backStack.lastOrNull() == HistoryRoute,
            onClick = {
                if (backStack.lastOrNull() != HistoryRoute) {
                    backStack.clear()
                    backStack.add(HistoryRoute)
                }
            }
        ),
        NavItem(
            label = "Stats",
            icon = Icons.Default.Info,
            isSelected = backStack.lastOrNull() == StatsRoute,
            onClick = {
                if (backStack.lastOrNull() != StatsRoute) {
                    backStack.clear()
                    backStack.add(StatsRoute)
                }
            }
        )
    )

    AppScaffold(
        title = "Cannabis Tracker",
        navItems = navItems
    ) { modifier ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                sessionsEntries(
                    viewModel = sessionsViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
                historyEntries(
                    viewModel = historyViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
                statsEntries(
                    viewModel = statsViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
            }
        )
    }
}
