package com.personalapps.suite.cannabis.di

import androidx.room.Room
import com.personalapps.suite.cannabis.data.CannabisDatabase
import com.personalapps.suite.cannabis.feature.history.di.historyModule
import com.personalapps.suite.cannabis.feature.sessions.di.sessionsModule
import com.personalapps.suite.cannabis.feature.stats.di.statsModule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val cannabisModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            CannabisDatabase::class.java,
            "cannabis.db"
        ).fallbackToDestructiveMigration().build()
    }

    // DAOs
    single { get<CannabisDatabase>().sessionsDao() }

    // Feature Modules
    includes(sessionsModule, historyModule, statsModule)
}
