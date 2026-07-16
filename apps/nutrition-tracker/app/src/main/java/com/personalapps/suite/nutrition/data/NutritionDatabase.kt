package com.personalapps.suite.nutrition.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.personalapps.suite.nutrition.feature.food.data.dao.FoodDao
import com.personalapps.suite.nutrition.feature.food.data.entities.FoodEntity
import com.personalapps.suite.nutrition.feature.meals.data.dao.MealDao
import com.personalapps.suite.nutrition.feature.meals.data.entities.MealEntity
import com.personalapps.suite.nutrition.feature.macros.data.dao.MacroGoalDao
import com.personalapps.suite.nutrition.feature.macros.data.entities.MacroGoalEntity
import com.personalapps.suite.nutrition.feature.history.data.dao.HistoryDao
import com.personalapps.suite.nutrition.feature.history.data.entities.HistoryEntryEntity
import com.personalapps.suite.shared.databaseutils.Converters

@Database(
    entities = [
        FoodEntity::class,
        MealEntity::class,
        MacroGoalEntity::class,
        HistoryEntryEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NutritionDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun macroGoalDao(): MacroGoalDao
    abstract fun historyDao(): HistoryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE foods ADD COLUMN gramsPerServing REAL NOT NULL DEFAULT 100.0")
            }
        }
    }
}
