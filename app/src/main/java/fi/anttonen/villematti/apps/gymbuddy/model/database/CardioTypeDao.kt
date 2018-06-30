/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioType

@Dao
interface CardioTypeDao {

    @Query("SELECT * FROM cardio_type ORDER BY cardio_type.name")
    fun getAll(): LiveData<List<CardioType>>

    @Query("SELECT * FROM cardio_type ORDER BY cardio_type.usageCount DESC, cardio_type.name ASC")
    fun getAllSortedByUsageCount(): LiveData<List<CardioType>>

    @Query("SELECT * FROM cardio_type WHERE cardio_type.id = :id")
    fun get(id: Long): CardioType

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cardioTypes: CardioType)

    @Update
    fun updateAll(vararg cardioTypes: CardioType)

    @Delete
    fun deleteAll(vararg cardioTypes: CardioType)
}