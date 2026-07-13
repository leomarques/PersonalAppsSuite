package com.personalapps.suite.workout.di

import androidx.room.Room
import com.personalapps.suite.workout.data.WorkoutDatabase
import com.personalapps.suite.workout.feature.exercises.di.exerciseModule
import com.personalapps.suite.workout.feature.workouts.di.workoutModule
import com.personalapps.suite.workout.feature.progress.di.progressModule
import org.koin.android.ext.koin.androidContext
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

    // Feature Modules
    includes(exerciseModule, workoutModule, progressModule)
}
