package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import fi.anttonen.villematti.apps.gymbuddy.WeightEntry

/**
 * Created by vma on 25/04/2018.
 */
interface GymEntriesDataSource {
    fun getGymEntries(): List<GymEntry>
    fun getGymEntry(id: String): GymEntry?
    fun getGymEntriesBefore(gymEntry: GymEntry, limit: Int, type: EntryType?) : MutableList<GymEntry>
    fun delete(gymEntry: GymEntry)
    fun update(gymEntry: GymEntry)
    fun add(gymEntry: GymEntry)
}