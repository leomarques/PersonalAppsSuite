package com.personalapps.suite.nutrition.feature.macros.di

import com.personalapps.suite.nutrition.feature.macros.data.repository.MacroGoalRepositoryImpl
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.macros.domain.usecase.SaveMacroGoalUseCase
import com.personalapps.suite.nutrition.feature.macros.presentation.MacroViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val macroModule = module {
    single<MacroGoalRepository> { MacroGoalRepositoryImpl(get()) }
    factoryOf(::SaveMacroGoalUseCase)
    viewModel { MacroViewModel(get(), get()) }
}
