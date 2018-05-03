package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface WeightEntryDao {

    @Query("SELECT * FROM weight_entry")
    fun getAll(): List<WeightEntry>

    @Insert
    fun insertAll(vararg weightEntries: WeightEntry)
}