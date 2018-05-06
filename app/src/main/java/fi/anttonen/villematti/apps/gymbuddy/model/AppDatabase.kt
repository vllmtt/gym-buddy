package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry

@Database(entities = [(WeightEntry::class), (CardioEntry::class)], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gymEntryDao(): WeightEntryDao
    abstract fun cardioEntryDao(): CardioEntryDao
}