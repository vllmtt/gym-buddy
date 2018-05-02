package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import fi.anttonen.villematti.apps.gymbuddy.model.WeightEntry
import org.joda.time.LocalDate
import java.util.*



class MockDataSource : GymEntriesDataSource {

    private lateinit var gymEntries:  MutableList<GymEntry>
    private var initialized = false

    override fun getGymEntries(): List<GymEntry> {
        if (!initialized) {
            gymEntries = mutableListOf()

            val weightData = arrayListOf(79.1, 79.1, 79.2, 79.1, 79.0, 78.8, 78.1, 78.9, 79.3, 79.7, 79.4, 79.6, 79.2, 79.4, 78.7, 78.9, 78.7, 79.7, 79.7, 79.4)

            for (i in 0 until 20) {
                gymEntries.add(WeightEntry("Entry $i", newDate(i), weightData[i]))
            }
            initialized = true

            sortByDateDescending()
        }

        return gymEntries
    }

    override fun getGymEntries(date: LocalDate): List<GymEntry> {
        if (!initialized) getGymEntries()
        return gymEntries.filter { entry -> entry.getEntryDate() == date }
    }

    private fun sortByDateDescending() {
        gymEntries.sortWith(kotlin.Comparator { e1, e2 -> e2.getEntryDate().compareTo(e1.getEntryDate()) })
    }

    override fun getGymEntry(id: String): GymEntry? {
        return gymEntries.find { entry -> entry.getEntryId() == id }
    }

    override fun getGymEntriesBefore(gymEntry: GymEntry, limit: Int, type: EntryType?): MutableList<GymEntry> {
        val entry = gymEntries.find { e -> e.getEntryId() == gymEntry.getEntryId() }
        val high = gymEntries.indexOf(entry)
        val low = minOf(high + limit, gymEntries.size)
        val sublist = mutableListOf<GymEntry>()
        sublist.addAll(gymEntries.subList(high, low))
        return sublist
    }

    override fun delete(gymEntry: GymEntry) {
        val entry = gymEntries.find { e -> e.getEntryId() == gymEntry.getEntryId() }
        gymEntries.remove(entry)
    }

    override fun update(gymEntry: GymEntry) {
        val entry = gymEntries.find { e -> e.getEntryId() == gymEntry.getEntryId() }
        entry!!.updateValuesFrom(gymEntry)
    }

    override fun add(gymEntry: GymEntry) {
        gymEntries.add(gymEntry)
    }

    private fun newDate(daysBeforeToday: Int): LocalDate {
        var date = LocalDate()
        date = date.minusDays(daysBeforeToday)
        return date
    }
}