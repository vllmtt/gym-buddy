package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import fi.anttonen.villematti.apps.gymbuddy.model.database.CardioEntryDao
import fi.anttonen.villematti.apps.gymbuddy.model.database.Converters
import fi.anttonen.villematti.apps.gymbuddy.model.database.WeightEntryDao
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry

@Database(entities = [(WeightEntry::class), (CardioEntry::class)], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gymEntryDao(): WeightEntryDao
    abstract fun cardioEntryDao(): CardioEntryDao
}