package com.personalapps.suite.nutrition.feature.history.di

import com.personalapps.suite.nutrition.feature.api.repository.HistoryRepository
import com.personalapps.suite.nutrition.feature.history.data.repository.HistoryRepositoryImpl
import com.personalapps.suite.nutrition.feature.history.domain.usecase.StartNewDayUseCase
import com.personalapps.suite.nutrition.feature.history.presentation.HistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val historyModule = module {
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    factory { StartNewDayUseCase(get(), get(), get(), get(), get(), get()) }
    viewModel { HistoryViewModel(get(), get(), get(), get()) }
}
