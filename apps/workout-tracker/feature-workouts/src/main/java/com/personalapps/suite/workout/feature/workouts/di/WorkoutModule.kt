package com.personalapps.suite.workout.feature.workouts.di

import com.personalapps.suite.workout.feature.workouts.data.repository.WorkoutRepositoryImpl
import com.personalapps.suite.workout.feature.api.repository.WorkoutRepository
import com.personalapps.suite.workout.feature.workouts.domain.usecase.CreateWorkoutSessionUseCase
import com.personalapps.suite.workout.feature.workouts.presentation.WorkoutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val workoutModule = module {
    single<WorkoutRepository> { WorkoutRepositoryImpl(get()) }
    factory { CreateWorkoutSessionUseCase(get()) }
    viewModel { WorkoutViewModel(get(), get(), get()) }
}
