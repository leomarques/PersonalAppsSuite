package com.personalapps.suite.workout.di

import androidx.room.Room
import com.personalapps.suite.workout.data.WorkoutDatabase
import com.personalapps.suite.workout.feature.exercises.data.repository.ExerciseRepositoryImpl
import com.personalapps.suite.workout.feature.exercises.domain.repository.ExerciseRepository
import com.personalapps.suite.workout.feature.exercises.presentation.ExerciseViewModel
import com.personalapps.suite.workout.feature.progress.presentation.ProgressViewModel
import com.personalapps.suite.workout.feature.workouts.data.repository.WorkoutRepositoryImpl
import com.personalapps.suite.workout.feature.workouts.domain.repository.WorkoutRepository
import com.personalapps.suite.workout.feature.workouts.presentation.WorkoutViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val workoutModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            WorkoutDatabase::class.java,
            "workout.db"
        ).fallbackToDestructiveMigration().build()
    }

    // DAOs
    single { get<WorkoutDatabase>().exerciseDao() }
    single { get<WorkoutDatabase>().workoutDao() }

    // Repositories
    single<ExerciseRepository> { ExerciseRepositoryImpl(get()) }
    single<WorkoutRepository> { WorkoutRepositoryImpl(get()) }

    // ViewModels
    viewModel { ExerciseViewModel(get()) }
    viewModel { WorkoutViewModel(get(), get()) }
    viewModel { ProgressViewModel(get(), get()) }
}
