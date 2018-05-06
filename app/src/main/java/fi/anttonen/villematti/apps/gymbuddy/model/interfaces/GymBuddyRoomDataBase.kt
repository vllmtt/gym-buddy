package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import android.arch.persistence.room.Room
import android.content.Context
import fi.anttonen.villematti.apps.gymbuddy.model.AppDatabase
import fi.anttonen.villematti.apps.gymbuddy.model.CardioEntryDao
import fi.anttonen.villematti.apps.gymbuddy.model.WeightEntryDao
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import org.joda.time.Duration
import org.joda.time.LocalDate

object GymBuddyRoomDataBase {

    var db: AppDatabase? = null
    lateinit var weightEntryDao: WeightEntryDao
    lateinit var cardioEntryDao: CardioEntryDao

    fun initIfNull(applicationContext: Context) {
        if (db == null) {
            db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "gym-database").fallbackToDestructiveMigration().build()
            weightEntryDao = db!!.gymEntryDao()
            cardioEntryDao = db!!.cardioEntryDao()
        }
    }

    fun initTestData() {
        val weightData = arrayListOf(79.1, 79.1, 79.2, 79.1, 79.0, 78.8, 78.1, 78.9, 79.3, 79.7, 79.4, 79.6, 79.2, 79.4, 78.7, 78.9, 78.7, 79.7, 79.7, 79.4)
        val weightEntries = mutableListOf<WeightEntry>()
        for (i in 0 until weightData.size) {
            var date = LocalDate()
            date = date.minusDays(i)
            weightEntries.add(WeightEntry(i.toLong(), date, weightData[i]))
        }
        weightEntryDao.insertAll(*weightEntries.toTypedArray())


        val distances = arrayListOf(12200, 2300, 5600, 7300, 5500, 5980, 3440, 5220, 9600, 11300, 15450)
        val cardioEntries = mutableListOf<CardioEntry>()
        for (i in 0 until distances.size) {
            val date = LocalDate().minusDays(i)
            val dura = Duration.standardMinutes(i * 10.toLong())
            cardioEntries.add(CardioEntry(i.toLong(), date, distances[i], dura))
        }
        cardioEntryDao.insertAll(*cardioEntries.toTypedArray())
    }
}