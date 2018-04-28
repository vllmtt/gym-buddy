package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

/**
 * Created by vma on 25/04/2018.
 */
interface GymEntriesDataSource {
    fun getGymEntries(): List<GymEntry>
    fun getGymEntriesBefore(gymEntry: GymEntry, limit: Int, type: EntryType?) : List<GymEntry>
}