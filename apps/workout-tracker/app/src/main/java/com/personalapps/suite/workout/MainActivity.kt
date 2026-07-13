package com.personalapps.suite.workout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.personalapps.suite.workout.feature.exercises.navigation.ExercisesRoute
import com.personalapps.suite.workout.feature.exercises.navigation.exerciseEntries
import com.personalapps.suite.workout.feature.exercises.presentation.ExerciseViewModel
import com.personalapps.suite.workout.feature.progress.navigation.ProgressRoute
import com.personalapps.suite.workout.feature.progress.navigation.progressEntries
import com.personalapps.suite.workout.feature.progress.presentation.ProgressViewModel
import com.personalapps.suite.workout.feature.workouts.navigation.DashboardRoute
import com.personalapps.suite.workout.feature.workouts.navigation.workoutEntries
import com.personalapps.suite.workout.feature.workouts.presentation.WorkoutViewModel
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

    val workoutViewModel: WorkoutViewModel = koinViewModel()
    val exerciseViewModel: ExerciseViewModel = koinViewModel()
    val progressViewModel: ProgressViewModel = koinViewModel()

    val navItems = listOf(
        NavItem(
            label = "Workouts",
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
            label = "Library",
            icon = Icons.Default.List,
            isSelected = backStack.lastOrNull() == ExercisesRoute,
            onClick = {
                if (backStack.lastOrNull() != ExercisesRoute) {
                    backStack.clear()
                    backStack.add(ExercisesRoute)
                }
            }
        ),
        NavItem(
            label = "Progress",
            icon = Icons.Default.Menu,
            isSelected = backStack.lastOrNull() == ProgressRoute,
            onClick = {
                if (backStack.lastOrNull() != ProgressRoute) {
                    backStack.clear()
                    backStack.add(ProgressRoute)
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
                workoutEntries(
                    viewModel = workoutViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    onNavigateToExercises = { backStack.add(ExercisesRoute) },
                    modifier = modifier
                )
                exerciseEntries(
                    viewModel = exerciseViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
                progressEntries(
                    viewModel = progressViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
            }
        )
    }
}
