package com.personalapps.suite.nutrition.data.database

import android.content.ContentValues
import android.util.Log
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.personalapps.suite.nutrition.feature.food.data.entities.FoodEntity
import com.personalapps.suite.shared.databaseutils.TransactionProvider
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.Executors

private val lenientJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            NutritionDatabase::class.java,
            "nutrition.db"
        ).addMigrations(
            NutritionDatabase.MIGRATION_1_2,
            NutritionDatabase.MIGRATION_2_3,
            NutritionDatabase.MIGRATION_3_4,
            NutritionDatabase.MIGRATION_4_5
        )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Executors.newSingleThreadExecutor().execute {
                    try {
                        Log.d("NutritionApp", "Pre-populating database...")
                        val context = androidContext()
                        val jsonString = context.assets.open("initial_foods.json").bufferedReader().use { it.readText() }
                        val foods = lenientJson.decodeFromString<List<FoodEntity>>(jsonString)
                        
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
                                    put("frequency", 0)
                                }
                                db.insert("foods", OnConflictStrategy.REPLACE, values)
                            }
                            db.setTransactionSuccessful()
                            Log.d("NutritionApp", "Database pre-populated successfully with ${foods.size} items.")
                        } finally {
                            db.endTransaction()
                        }
                    } catch (e: Exception) {
                        Log.e("NutritionApp", "Error pre-populating database", e)
                        e.printStackTrace()
                    }
                }
            }
        }).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    single<TransactionProvider> { TransactionProviderImpl(get()) }

    // DAOs
    single { get<NutritionDatabase>().foodDao() }
    single { get<NutritionDatabase>().mealDao() }
    single { get<NutritionDatabase>().macroGoalDao() }
    single { get<NutritionDatabase>().historyDao() }
}
