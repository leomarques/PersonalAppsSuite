package com.personalapps.suite.nutrition.feature.food.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personalapps.suite.nutrition.feature.food.data.entities.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM foods ORDER BY name ASC")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getFoodById(id: Long): FoodEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity): Long

    @Update
    suspend fun updateFood(food: FoodEntity): Int

    @Query("UPDATE foods SET frequency = frequency + 1 WHERE id = :foodId")
    suspend fun incrementFrequency(foodId: Long)

    @Query("UPDATE foods SET frequency = frequency + 1 WHERE name = :name")
    suspend fun incrementFrequencyByName(name: String)

    @Delete
    suspend fun deleteFood(food: FoodEntity): Int
}
