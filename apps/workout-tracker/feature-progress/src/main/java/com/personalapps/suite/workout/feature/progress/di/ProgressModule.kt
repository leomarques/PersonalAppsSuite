package com.personalapps.suite.workout.feature.progress.di

import com.personalapps.suite.workout.feature.progress.domain.usecase.GetProgressPointsUseCase
import com.personalapps.suite.workout.feature.progress.presentation.ProgressViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val progressModule = module {
    factory { GetProgressPointsUseCase() }
    viewModel { ProgressViewModel(get(), get(), get()) }
}
