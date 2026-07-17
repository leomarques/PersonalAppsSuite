package com.personalapps.suite.nutrition.feature.meals.di

import com.personalapps.suite.nutrition.feature.meals.data.repository.MealRepositoryImpl
import com.personalapps.suite.nutrition.feature.api.repository.MealRepository
import com.personalapps.suite.nutrition.feature.meals.domain.usecase.LogMealUseCase
import com.personalapps.suite.nutrition.feature.meals.presentation.MealViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mealModule = module {
    single<MealRepository> { MealRepositoryImpl(get()) }
    factory { LogMealUseCase(get(), get(), get()) }
    viewModel { MealViewModel(get(), get(), get()) }
}
