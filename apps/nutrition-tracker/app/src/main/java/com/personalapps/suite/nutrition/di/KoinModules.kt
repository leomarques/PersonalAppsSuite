package com.personalapps.suite.nutrition.di

import com.personalapps.suite.nutrition.data.database.databaseModule
import com.personalapps.suite.nutrition.feature.food.di.foodModule
import com.personalapps.suite.nutrition.feature.history.di.historyModule
import com.personalapps.suite.nutrition.feature.macros.di.macroModule
import com.personalapps.suite.nutrition.feature.meals.di.mealModule
import com.personalapps.suite.shared.common.DateProvider
import com.personalapps.suite.shared.common.RealDateProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val nutritionModule = module {
    // Common
    single<DateProvider> { RealDateProvider() }

    // Preferences
    single<com.personalapps.suite.shared.preferences.PreferencesManager> { 
        com.personalapps.suite.shared.preferences.PreferencesManagerImpl(androidContext()) 
    }

    // Feature Modules
    includes(databaseModule, foodModule, mealModule, macroModule, historyModule)
}
