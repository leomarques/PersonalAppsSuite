package com.personalapps.suite.cannabis

import android.app.Application
import com.personalapps.suite.cannabis.di.cannabisModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CannabisApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@CannabisApp)
            modules(cannabisModule)
        }
    }
}
