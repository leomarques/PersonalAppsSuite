package com.personalapps.suite.cannabis.feature.sessions.di

import com.personalapps.suite.cannabis.feature.sessions.data.repository.SessionsRepositoryImpl
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import com.personalapps.suite.cannabis.feature.sessions.domain.usecase.StartSessionUseCase
import com.personalapps.suite.cannabis.feature.sessions.presentation.SessionsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sessionsModule = module {
    single<SessionsRepository> { SessionsRepositoryImpl(get()) }
    factory { StartSessionUseCase(get()) }
    viewModel { SessionsViewModel(get(), get()) }
}
