package com.personalapps.suite.nutrition.feature.history.di

import com.personalapps.suite.nutrition.feature.history.presentation.HistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val historyModule = module {
    viewModel { HistoryViewModel(get(), get()) }
}
