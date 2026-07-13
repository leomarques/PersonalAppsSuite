package com.personalapps.suite.cannabis.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.personalapps.suite.shared.databaseutils.Converters
import com.personalapps.suite.cannabis.feature.sessions.data.dao.SessionsDao
import com.personalapps.suite.cannabis.feature.sessions.data.entities.CannabisLogEntity
import com.personalapps.suite.cannabis.feature.sessions.data.entities.CannabisSessionEntity

@Database(
    entities = [
        CannabisSessionEntity::class,
        CannabisLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CannabisDatabase : RoomDatabase() {
    abstract fun sessionsDao(): SessionsDao
}
