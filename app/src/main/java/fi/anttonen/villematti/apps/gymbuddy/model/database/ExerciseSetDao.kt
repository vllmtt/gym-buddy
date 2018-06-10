/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import fi.anttonen.villematti.apps.gymbuddy.model.entity.ExerciseSet

//@Dao
interface ExerciseSetDao {
    //@Query("SELECT * FROM exercise_set")
    fun getAll(): LiveData<List<ExerciseSet>>

    //@Query("SELECT * FROM exercise_set WHERE exercise_set.id = :id")
    fun get(id: Long): ExerciseSet

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cardioEntries: ExerciseSet)

    //@Update
    fun updateAll(vararg cardioEntries: ExerciseSet)

    //@Delete
    fun deleteAll(vararg cardioEntries: ExerciseSet)
}