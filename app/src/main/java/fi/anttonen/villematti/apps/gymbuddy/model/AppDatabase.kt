package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry

@Database(entities = [(WeightEntry::class)], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gymEntryDao(): GymEntryDao
}