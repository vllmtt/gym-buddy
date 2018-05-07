package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import org.joda.time.LocalDate

@Dao
interface WeightEntryDao {

    @Query("SELECT * FROM weight_entry")
    fun getAll(): LiveData<List<WeightEntry>>

    @Query("SELECT * FROM weight_entry WHERE weight_entry.date = :date")
    fun getAll(date: LocalDate): LiveData<List<WeightEntry>>

    @Query("SELECT * FROM weight_entry WHERE weight_entry.id = :id")
    fun get(id: Long): WeightEntry?

    @Query("SELECT * FROM weight_entry WHERE weight_entry.date <= :date ORDER BY weight_entry.date DESC LIMIT :limit")
    fun getHistory(date: LocalDate, limit: Int): List<WeightEntry>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg weightEntries: WeightEntry)

    @Update
    fun updateAll(vararg weightEntries: WeightEntry)

    @Delete
    fun deleteAll(vararg weightEntries: WeightEntry)
}