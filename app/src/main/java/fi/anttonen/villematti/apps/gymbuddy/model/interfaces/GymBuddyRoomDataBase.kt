package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import android.arch.persistence.room.Room
import android.content.Context
import fi.anttonen.villematti.apps.gymbuddy.model.AppDatabase
import fi.anttonen.villematti.apps.gymbuddy.model.GymEntryDao
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import org.joda.time.LocalDate

object GymBuddyRoomDataBase {

    var db: AppDatabase? = null

    fun initIfNull(applicationContext: Context) {
        if (db == null) {
            db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "gym-database").build()
        }
    }
}