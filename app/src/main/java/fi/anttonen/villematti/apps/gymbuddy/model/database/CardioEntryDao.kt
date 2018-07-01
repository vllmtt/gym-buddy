/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import org.joda.time.LocalDate

@Dao
interface CardioEntryDao {
    @Query("SELECT * FROM cardio_entry")
    fun getAll(): LiveData<List<CardioEntry>>

    @Query("SELECT * FROM cardio_entry WHERE cardio_entry.date = :date")
    fun getAll(date: LocalDate): LiveData<List<CardioEntry>>

    @Query("SELECT * FROM cardio_entry WHERE cardio_entry.date <= :start AND cardio_entry.date >= :end ORDER BY cardio_entry.date DESC")
    fun getAll(start: LocalDate, end: LocalDate): LiveData<List<CardioEntry>>

    @Query("SELECT * FROM cardio_entry WHERE cardio_entry.id = :id")
    fun get(id: Long): CardioEntry

    @Query("SELECT * FROM cardio_entry WHERE cardio_entry.date < :date ORDER BY cardio_entry.date LIMIT :limit")
    fun getHistory(date: LocalDate, limit: Int): LiveData<List<CardioEntry>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cardioEntries: CardioEntry)

    @Update
    fun updateAll(vararg cardioEntries: CardioEntry)

    @Delete
    fun deleteAll(vararg cardioEntries: CardioEntry)

    @Query("DELETE FROM cardio_entry WHERE cardio_entry.cardioTypeId = :id")
    fun removeIfExerciseIdEquals(id: Long)
}