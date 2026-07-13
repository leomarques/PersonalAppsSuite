package com.personalapps.suite.workout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.personalapps.suite.shared.designsystem.PersonalAppsSuiteTheme
import com.personalapps.suite.workout.feature.exercises.presentation.ExerciseScreen
import com.personalapps.suite.workout.feature.exercises.presentation.ExerciseViewModel
import com.personalapps.suite.workout.feature.progress.presentation.ProgressScreen
import com.personalapps.suite.workout.feature.progress.presentation.ProgressViewModel
import com.personalapps.suite.workout.feature.workouts.presentation.WorkoutScreen
import com.personalapps.suite.workout.feature.workouts.presentation.WorkoutViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

import com.personalapps.suite.shared.navigation.Destination

@Serializable data object DashboardRoute : Destination
@Serializable data object ExercisesRoute : Destination
@Serializable data object ProgressRoute : Destination

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

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(DashboardRoute)

    val workoutViewModel: WorkoutViewModel = koinViewModel()
    val exerciseViewModel: ExerciseViewModel = koinViewModel()
    val progressViewModel: ProgressViewModel = koinViewModel()

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
                    icon = { Icon(Icons.Default.Home, contentDescription = "Workouts") },
                    label = { Text("Workouts") }
                )
                NavigationBarItem(
                    selected = backStack.lastOrNull() == ExercisesRoute,
                    onClick = {
                        if (backStack.lastOrNull() != ExercisesRoute) {
                            backStack.clear()
                            backStack.add(ExercisesRoute)
                        }
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = "Library") },
                    label = { Text("Library") }
                )
                NavigationBarItem(
                    selected = backStack.lastOrNull() == ProgressRoute,
                    onClick = {
                        if (backStack.lastOrNull() != ProgressRoute) {
                            backStack.clear()
                            backStack.add(ProgressRoute)
                        }
                    },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Progress") },
                    label = { Text("Progress") }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<DashboardRoute> {
                    WorkoutScreen(
                        viewModel = workoutViewModel,
                        onBackClick = { backStack.removeLastOrNull() },
                        onNavigateToExercises = { backStack.add(ExercisesRoute) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                entry<ExercisesRoute> {
                    ExerciseScreen(
                        viewModel = exerciseViewModel,
                        onBackClick = { backStack.removeLastOrNull() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                entry<ProgressRoute> {
                    ProgressScreen(
                        viewModel = progressViewModel,
                        onBackClick = { backStack.removeLastOrNull() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        )
    }
}
