package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import org.joda.time.LocalDate

@Dao
interface GymEntryDao {

    @Query("SELECT * FROM weight_entry")
    fun getAll(): LiveData<List<WeightEntry>>

    @Query("SELECT * FROM weight_entry WHERE weight_entry.date = :date")
    fun getAll(date: LocalDate): LiveData<List<WeightEntry>>

    @Query("SELECT * FROM weight_entry WHERE weight_entry.id = :id")
    fun get(id: Long): LiveData<WeightEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg weightEntries: WeightEntry)
}