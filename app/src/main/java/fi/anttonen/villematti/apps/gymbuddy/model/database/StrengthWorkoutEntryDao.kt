/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthWorkoutEntry
import org.joda.time.LocalDate

@Dao
interface StrengthWorkoutEntryDao {
    @Query("SELECT * FROM strength_workout_entry")
    fun getAll(): LiveData<List<StrengthWorkoutEntry>>

    @Query("SELECT * FROM strength_workout_entry WHERE strength_workout_entry.date = :date")
    fun getAll(date: LocalDate): LiveData<List<StrengthWorkoutEntry>>

    @Query("SELECT * FROM strength_workout_entry WHERE strength_workout_entry.date <= :start AND strength_workout_entry.date >= :end ORDER BY strength_workout_entry.date DESC")
    fun getAll(start: LocalDate, end: LocalDate): LiveData<List<StrengthWorkoutEntry>>

    @Query("SELECT * FROM strength_workout_entry WHERE strength_workout_entry.id = :id")
    fun get(id: Long): StrengthWorkoutEntry

    @Query("SELECT * FROM strength_workout_entry WHERE strength_workout_entry.date < :date ORDER BY strength_workout_entry.date LIMIT :limit")
    fun getHistory(date: LocalDate, limit: Int): LiveData<List<StrengthWorkoutEntry>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cardioEntries: StrengthWorkoutEntry)

    @Update
    fun updateAll(vararg cardioEntries: StrengthWorkoutEntry)

    @Delete
    fun deleteAll(vararg cardioEntries: StrengthWorkoutEntry)
}