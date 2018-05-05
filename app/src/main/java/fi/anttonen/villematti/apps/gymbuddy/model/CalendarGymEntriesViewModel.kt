package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.*
import fi.anttonen.villematti.apps.gymbuddy.R.string.date
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymBuddyRoomDataBase
import org.joda.time.LocalDate

class CalendarGymEntriesViewModel : ViewModel() {
    companion object {
        const val WEIGHT_HISTORY_LIMIT = 5
    }

    private var weightEntryLiveData: LiveData<List<WeightEntry>>? = null
    private var historyWeightEntryLiveData: LiveData<List<WeightEntry>>? = null

    private val dateFilterLiveData: MutableLiveData<LocalDate> = MutableLiveData()

    fun getWeightEntriesForDate(): LiveData<List<WeightEntry>> {
        if (weightEntryLiveData == null) {
            weightEntryLiveData = Transformations.switchMap(dateFilterLiveData) { date ->
                GymBuddyRoomDataBase.weightEntryDao.getAll(date)
            }
        }
        return weightEntryLiveData!!
    }

    fun getGymEntriesForDate(): MediatorLiveData<List<GymEntry>> {
        if (weightEntryLiveData == null) {
            weightEntryLiveData = Transformations.switchMap(dateFilterLiveData) { date ->
                GymBuddyRoomDataBase.weightEntryDao.getAll(date)
            }
        }

        return MediatorLiveData<List<GymEntry>>().apply {
            var weightEntries: List<WeightEntry>? = null

            fun update() {
                val localWeightEntries = weightEntries
                if (localWeightEntries != null) {
                    val gymEntries = mutableListOf<GymEntry>()
                    gymEntries.addAll(localWeightEntries)
                    this.value = gymEntries
                }
            }

            addSource(weightEntryLiveData!!) {
                weightEntries = it
                update()
            }
        }
    }

    fun getWeightEntryHistoryForDate(): LiveData<List<WeightEntry>> {
        if (historyWeightEntryLiveData == null) {
            historyWeightEntryLiveData = Transformations.switchMap(dateFilterLiveData) { date ->
                GymBuddyRoomDataBase.weightEntryDao.getHistory(date, WEIGHT_HISTORY_LIMIT)
            }
        }
        return historyWeightEntryLiveData!!
    }

    fun setDateFilter(date: LocalDate?) {
        dateFilterLiveData.value = date
    }
}