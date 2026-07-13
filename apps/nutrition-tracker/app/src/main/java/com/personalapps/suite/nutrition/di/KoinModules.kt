package com.personalapps.suite.nutrition.di

import androidx.room.Room
import com.personalapps.suite.nutrition.data.NutritionDatabase
import com.personalapps.suite.nutrition.feature.food.di.foodModule
import com.personalapps.suite.nutrition.feature.history.di.historyModule
import com.personalapps.suite.nutrition.feature.macros.di.macroModule
import com.personalapps.suite.nutrition.feature.meals.di.mealModule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val nutritionModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            NutritionDatabase::class.java,
            "nutrition.db"
        ).fallbackToDestructiveMigration().build()
    }

    // DAOs
    single { get<NutritionDatabase>().foodDao() }
    single { get<NutritionDatabase>().mealDao() }
    single { get<NutritionDatabase>().macroGoalDao() }

    // Feature Modules
    includes(foodModule, mealModule, macroModule, historyModule)
}
