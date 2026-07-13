package com.personalapps.suite.cannabis.feature.stats.di

import com.personalapps.suite.cannabis.feature.stats.presentation.StatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val statsModule = module {
    viewModel { StatsViewModel(get()) }
}
