package com.personalapps.suite.cannabis.feature.history.di

import com.personalapps.suite.cannabis.feature.history.presentation.HistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val historyModule = module {
    viewModel { HistoryViewModel(get()) }
}
