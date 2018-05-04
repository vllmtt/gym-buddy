package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import org.joda.time.LocalDate

@Dao
interface WeightEntryDao {

    @Query("SELECT * FROM weight_entry")
    fun getAll(): List<WeightEntry>

    @Query("SELECT * FROM weight_entry WHERE date IN :date")
    fun getAll(date: LocalDate): List<WeightEntry>

    @Query("SELECT * FROM weight_entry WHERE id IN :id")
    fun get(id: Long): List<WeightEntry>

    @Insert
    fun insertAll(vararg weightEntries: WeightEntry)
}