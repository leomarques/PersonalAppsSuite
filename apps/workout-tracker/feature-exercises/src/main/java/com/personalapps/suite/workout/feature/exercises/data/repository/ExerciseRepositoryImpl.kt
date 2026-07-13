package com.personalapps.suite.workout.feature.exercises.data.repository

import com.personalapps.suite.workout.feature.exercises.data.dao.ExerciseDao
import com.personalapps.suite.workout.feature.exercises.data.entities.ExerciseEntity
import com.personalapps.suite.workout.feature.exercises.domain.model.Exercise
import com.personalapps.suite.workout.feature.exercises.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun ExerciseEntity.toDomain() = Exercise(id = id, name = name, category = category)
fun Exercise.toEntity() = ExerciseEntity(id = id, name = name, category = category)

class ExerciseRepositoryImpl(private val exerciseDao: ExerciseDao) : ExerciseRepository {
    override fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises().map { list -> list.map { it.toDomain() } }
    override suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise.toEntity())
    override suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.deleteExercise(exercise.toEntity())
    }
}
