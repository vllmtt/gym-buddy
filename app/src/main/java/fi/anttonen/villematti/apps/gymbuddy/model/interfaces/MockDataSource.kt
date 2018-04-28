package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import android.util.Log
import fi.anttonen.villematti.apps.gymbuddy.WeightEntry
import java.util.*



class MockDataSource : GymEntriesDataSource {

    private lateinit var gymEntries:  MutableList<GymEntry>
    var initialized = false

    override fun getGymEntries(): List<GymEntry> {
        if (!initialized) {
            gymEntries = mutableListOf()

            val weightData = arrayListOf(79.1, 79.1, 79.2, 79.1, 79.0, 78.8, 78.1, 78.9, 79.3, 79.7, 79.4, 79.6, 79.2, 79.4, 78.7, 78.9, 78.7, 79.7, 79.7, 79.4)

            for (i in 0 until 20) {
                gymEntries.add(WeightEntry("Entry $i", newDate(i), weightData[i]))
            }
            initialized = true
            
            gymEntries.sortWith(kotlin.Comparator { e1, e2 -> e1.getEntryDate().compareTo(e2.getEntryDate()) })
        }

        return gymEntries
    }

    override fun getGymEntry(id: String): GymEntry? {
        return gymEntries.find { entry -> entry.getEntryId() == id }
    }

    override fun getGymEntriesBefore(gymEntry: GymEntry, limit: Int, entryType: EntryType?): List<GymEntry> {
        val high = gymEntries.indexOf(gymEntry)
        val low = minOf(high + limit, gymEntries.size)
        return gymEntries.subList(high, low)
    }

    private fun randomDoubleBetween(min: Double, max: Double): Double {
        val r = Random()
        return min + (max - min) * r.nextDouble()
    }

    private fun newDate(daysBeforeToday: Int): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -daysBeforeToday)
        return cal.time
    }
}