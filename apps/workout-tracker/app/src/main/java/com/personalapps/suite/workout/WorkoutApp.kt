package com.personalapps.suite.workout

import android.app.Application
import com.personalapps.suite.workout.di.workoutModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WorkoutApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@WorkoutApp)
            modules(workoutModule)
        }
    }
}
