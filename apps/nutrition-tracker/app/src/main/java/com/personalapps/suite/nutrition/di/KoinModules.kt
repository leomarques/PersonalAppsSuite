package com.personalapps.suite.nutrition.di

import android.content.ContentValues
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.personalapps.suite.nutrition.data.NutritionDatabase
import com.personalapps.suite.nutrition.feature.food.data.entities.FoodEntity
import com.personalapps.suite.nutrition.feature.food.di.foodModule
import com.personalapps.suite.nutrition.feature.history.di.historyModule
import com.personalapps.suite.nutrition.feature.macros.di.macroModule
import com.personalapps.suite.nutrition.feature.meals.di.mealModule
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.Executors

val nutritionModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            NutritionDatabase::class.java,
            "nutrition.db"
        ).addMigrations(NutritionDatabase.MIGRATION_1_2, NutritionDatabase.MIGRATION_2_3)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Executors.newSingleThreadExecutor().execute {
                    try {
                        val context = androidContext()
                        val jsonString = context.assets.open("initial_foods.json").bufferedReader().use { it.readText() }
                        val foods = Json.decodeFromString<List<FoodEntity>>(jsonString)
                        db.beginTransaction()
                        try {
                            foods.forEach { food ->
                                val values = ContentValues().apply {
                                    put("name", food.name)
                                    put("calories", food.calories)
                                    put("protein", food.protein)
                                    put("carbs", food.carbs)
                                    put("fat", food.fat)
                                    put("gramsPerServing", food.gramsPerServing)
                                }
                                db.insert("foods", OnConflictStrategy.REPLACE, values)
                            }
                            db.setTransactionSuccessful()
                        } finally {
                            db.endTransaction()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    // DAOs
    single { get<NutritionDatabase>().foodDao() }
    single { get<NutritionDatabase>().mealDao() }
    single { get<NutritionDatabase>().macroGoalDao() }
    single { get<NutritionDatabase>().historyDao() }

    // Feature Modules
    includes(foodModule, mealModule, macroModule, historyModule)
}
