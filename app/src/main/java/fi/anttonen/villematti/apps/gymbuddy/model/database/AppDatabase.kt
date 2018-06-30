/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import fi.anttonen.villematti.apps.gymbuddy.model.database.CardioEntryDao
import fi.anttonen.villematti.apps.gymbuddy.model.database.Converters
import fi.anttonen.villematti.apps.gymbuddy.model.database.WeightEntryDao
import fi.anttonen.villematti.apps.gymbuddy.model.entity.*

@Database(entities = [
    (WeightEntry::class),
    (CardioEntry::class),
    (CardioType::class),
    (StrengthWorkoutEntry::class),
    (StrengthExercise::class)], version = 10)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gymEntryDao(): WeightEntryDao
    abstract fun cardioEntryDao(): CardioEntryDao
    abstract fun cardioTypeDao(): CardioTypeDao
    abstract fun strengthWorkoutEntryDao(): StrengthWorkoutEntryDao
    abstract fun strengthExerciseDao(): StrengthExerciseDao
}