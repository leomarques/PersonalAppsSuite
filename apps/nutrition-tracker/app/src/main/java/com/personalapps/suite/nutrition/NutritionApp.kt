package com.personalapps.suite.nutrition

import android.app.Application
import com.personalapps.suite.nutrition.di.nutritionModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NutritionApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@NutritionApp)
            modules(nutritionModule)
        }
    }
}
