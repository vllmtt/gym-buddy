package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.persistence.room.Room
import android.content.Context
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
            val entry = WeightEntry(i.toLong(), date)
            entry.setWeight(weightData[i], false)
            weightEntries.add(WeightEntry(i.toLong(), date))
        }
        weightEntryDao.insertAll(*weightEntries.toTypedArray())


        val distances = arrayListOf(12200.0, 2300.0, 5600.0, 7300.0, 5500.0, 5980.0, 3440.0, 5220.0, 9600.0, 11300.0, 15450.0)
        val cardioEntries = mutableListOf<CardioEntry>()
        for (i in 0 until distances.size) {
            val date = LocalDate().minusDays(i)
            val dura = Duration.standardMinutes(i * 10.toLong())
            cardioEntries.add(CardioEntry(i.toLong(), date, distances[i], dura))
        }
        cardioEntryDao.insertAll(*cardioEntries.toTypedArray())
    }
}