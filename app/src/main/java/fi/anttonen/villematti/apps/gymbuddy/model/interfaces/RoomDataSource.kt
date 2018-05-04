package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import android.arch.persistence.room.Room
import android.content.Context
import fi.anttonen.villematti.apps.gymbuddy.model.AppDatabase
import fi.anttonen.villematti.apps.gymbuddy.model.WeightEntryDao
import org.joda.time.LocalDate

class RoomDataSource : GymEntriesDataSource {

    private var db: AppDatabase? = null
    private var weightEntryDao: WeightEntryDao? = null

    fun init(applicationContext: Context) {
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "gym-database").build()
        weightEntryDao = (db as AppDatabase).weightEntryDao()
    }


    override fun getGymEntries(): List<GymEntry> {
        val weightEntries = weightEntryDao!!.getAll()
        return weightEntries
    }

    override fun getGymEntries(date: LocalDate): List<GymEntry> {
        val weightEntries = weightEntryDao!!.getAll(date)
        return weightEntries
    }

    override fun getGymEntry(id: Long): GymEntry? {
        val list = weightEntryDao!!.get(id)
        if (!list.isEmpty()) return list.first()
        return null
    }

    override fun getGymEntriesBefore(gymEntry: GymEntry, limit: Int, type: EntryType?): MutableList<GymEntry> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(gymEntry: GymEntry) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(gymEntry: GymEntry) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun add(gymEntry: GymEntry) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}