package com.personalapps.suite.cannabis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.personalapps.suite.cannabis.ui.SettingsScreen
import com.personalapps.suite.cannabis.ui.SettingsViewModel
import com.personalapps.suite.cannabis.ui.StatsScreen
import com.personalapps.suite.cannabis.ui.TrackerScreen
import com.personalapps.suite.cannabis.ui.TrackerViewModel
import com.personalapps.suite.shared.designsystem.PersonalAppsSuiteTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            

            PersonalAppsSuiteTheme {
                LeedApp(settingsViewModel = settingsViewModel)
            }
        }
    }
}

@Composable
fun LeedApp(
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val trackerViewModel: TrackerViewModel = viewModel()

    NavHost(navController = navController, startDestination = "tracker") {
        composable("tracker") {
            TrackerScreen(
                viewModel = trackerViewModel,
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("stats") {
            StatsScreen(
                viewModel = trackerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
