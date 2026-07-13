package com.personalapps.suite.workout.feature.exercises.di

import com.personalapps.suite.workout.feature.exercises.data.repository.ExerciseRepositoryImpl
import com.personalapps.suite.workout.feature.api.repository.ExerciseRepository
import com.personalapps.suite.workout.feature.exercises.presentation.ExerciseViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val exerciseModule = module {
    single<ExerciseRepository> { ExerciseRepositoryImpl(get()) }
    viewModel { ExerciseViewModel(get()) }
}
