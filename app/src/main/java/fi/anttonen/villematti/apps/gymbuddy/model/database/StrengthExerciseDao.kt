/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthExercise

@Dao
interface StrengthExerciseDao {
    @Query("SELECT * FROM strength_exercise ORDER BY strength_exercise.name")
    fun getAll(): LiveData<List<StrengthExercise>>

    @Query("SELECT * FROM strength_exercise WHERE strength_exercise.id = :id")
    fun get(id: Long): StrengthExercise

    @Query("SELECT * FROM strength_exercise ORDER BY strength_exercise.usageCount DESC, strength_exercise.name ASC")
    fun getAllSortedByUsageCount(): LiveData<List<StrengthExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cardioEntries: StrengthExercise)

    @Update
    fun updateAll(vararg cardioEntries: StrengthExercise)

    @Delete
    fun deleteAll(vararg cardioEntries: StrengthExercise)

}