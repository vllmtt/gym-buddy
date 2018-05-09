package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
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
    fun get(id: Long): LiveData<CardioEntry>

    @Query("SELECT * FROM cardio_entry WHERE cardio_entry.date < :date ORDER BY cardio_entry.date LIMIT :limit")
    fun getHistory(date: LocalDate, limit: Int): LiveData<List<CardioEntry>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cardioEntries: CardioEntry)
}