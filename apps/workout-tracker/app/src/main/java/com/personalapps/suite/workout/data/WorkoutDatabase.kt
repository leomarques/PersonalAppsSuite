package com.personalapps.suite.workout.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.personalapps.suite.shared.databaseutils.Converters
import com.personalapps.suite.workout.feature.exercises.data.dao.ExerciseDao
import com.personalapps.suite.workout.feature.exercises.data.entities.ExerciseEntity
import com.personalapps.suite.workout.feature.workouts.data.dao.WorkoutDao
import com.personalapps.suite.workout.feature.workouts.data.entities.WorkoutSessionEntity
import com.personalapps.suite.workout.feature.workouts.data.entities.WorkoutSetEntity

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
}
