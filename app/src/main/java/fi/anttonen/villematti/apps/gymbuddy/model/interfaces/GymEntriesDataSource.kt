package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import org.joda.time.LocalDate
import java.util.*

/**
 * Created by vma on 25/04/2018.
 */
interface GymEntriesDataSource {
    fun getGymEntries(): List<GymEntry>
    fun getGymEntries(date: LocalDate): List<GymEntry>
    fun getGymEntry(id: String): GymEntry?
    fun getGymEntriesBefore(gymEntry: GymEntry, limit: Int, type: EntryType?) : MutableList<GymEntry>
    fun delete(gymEntry: GymEntry)
    fun update(gymEntry: GymEntry)
    fun add(gymEntry: GymEntry)
}