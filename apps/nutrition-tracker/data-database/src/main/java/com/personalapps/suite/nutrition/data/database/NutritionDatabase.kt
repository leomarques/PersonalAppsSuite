package com.personalapps.suite.nutrition.data.database

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
    version = 7,
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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE foods ADD COLUMN frequency INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE meals_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, loggedFoodsJson TEXT NOT NULL)")
                db.execSQL("INSERT INTO meals_new (id, name, loggedFoodsJson) SELECT id, name, loggedFoodsJson FROM meals")
                db.execSQL("DROP TABLE meals")
                db.execSQL("ALTER TABLE meals_new RENAME TO meals")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE meals ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE foods ADD COLUMN lastUsedAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Recreate foods table without frequency
                db.execSQL("CREATE TABLE foods_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, calories INTEGER NOT NULL, protein REAL NOT NULL, carbs REAL NOT NULL, fat REAL NOT NULL, gramsPerServing REAL NOT NULL DEFAULT 100.0, lastUsedAt INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("INSERT INTO foods_new (id, name, calories, protein, carbs, fat, gramsPerServing, lastUsedAt) SELECT id, name, calories, protein, carbs, fat, gramsPerServing, lastUsedAt FROM foods")
                db.execSQL("DROP TABLE foods")
                db.execSQL("ALTER TABLE foods_new RENAME TO foods")

                // Recreate history_entries without goals
                db.execSQL("CREATE TABLE history_entries_new (date TEXT PRIMARY KEY NOT NULL, totalCalories INTEGER NOT NULL, totalProtein REAL NOT NULL, totalCarbs REAL NOT NULL, totalFat REAL NOT NULL)")
                db.execSQL("INSERT INTO history_entries_new (date, totalCalories, totalProtein, totalCarbs, totalFat) SELECT date, totalCalories, totalProtein, totalCarbs, totalFat FROM history_entries")
                db.execSQL("DROP TABLE history_entries")
                db.execSQL("ALTER TABLE history_entries_new RENAME TO history_entries")
            }
        }
    }
}
