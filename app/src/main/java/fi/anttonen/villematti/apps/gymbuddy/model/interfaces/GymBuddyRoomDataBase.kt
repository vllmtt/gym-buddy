package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import android.arch.persistence.room.Room
import android.content.Context
import fi.anttonen.villematti.apps.gymbuddy.model.AppDatabase
import fi.anttonen.villematti.apps.gymbuddy.model.GymEntryDao
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import org.joda.time.LocalDate

object GymBuddyRoomDataBase {

    var db: AppDatabase? = null
    lateinit var gymEntryDao: GymEntryDao

    fun initIfNull(applicationContext: Context) {
        if (db == null) {
            db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "gym-database").build()
            gymEntryDao = db!!.gymEntryDao()
        }
    }

    fun initTestData() {
        val weightData = arrayListOf(79.1, 79.1, 79.2, 79.1, 79.0, 78.8, 78.1, 78.9, 79.3, 79.7, 79.4, 79.6, 79.2, 79.4, 78.7, 78.9, 78.7, 79.7, 79.7, 79.4)
        val weightEntries = mutableListOf<WeightEntry>()
        for (i in 0 until 20) {
            var date = LocalDate()
            date = date.minusDays(i)
            weightEntries.add(WeightEntry(i.toLong(), date, weightData[i]))
        }
        gymEntryDao.insertAll(*weightEntries.toTypedArray())
    }
}