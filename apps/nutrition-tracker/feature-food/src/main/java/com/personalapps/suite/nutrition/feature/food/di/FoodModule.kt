package com.personalapps.suite.nutrition.feature.food.di

import com.personalapps.suite.nutrition.feature.food.data.repository.FoodRepositoryImpl
import com.personalapps.suite.nutrition.feature.api.repository.FoodRepository
import com.personalapps.suite.nutrition.feature.food.domain.usecase.AddFoodUseCase
import com.personalapps.suite.nutrition.feature.food.domain.usecase.DeleteFoodUseCase
import com.personalapps.suite.nutrition.feature.food.domain.usecase.UpdateFoodUseCase
import com.personalapps.suite.nutrition.feature.food.presentation.FoodViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val foodModule = module {
    single<FoodRepository> { FoodRepositoryImpl(get()) }
    factoryOf(::AddFoodUseCase)
    factoryOf(::UpdateFoodUseCase)
    factoryOf(::DeleteFoodUseCase)
    viewModel { FoodViewModel(get(), get(), get(), get()) }
}
