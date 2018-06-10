/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioType

@Dao
interface CardioTypeDao {

    @Query("SELECT * FROM cardio_type ORDER BY cardio_type.name")
    fun getAll(): LiveData<List<CardioType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cardioTypes: CardioType)
}