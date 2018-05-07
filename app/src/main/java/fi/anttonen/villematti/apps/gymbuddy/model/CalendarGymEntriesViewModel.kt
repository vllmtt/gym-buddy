package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.*
import android.util.Log
import fi.anttonen.villematti.apps.gymbuddy.R.string.date
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymBuddyRoomDataBase
import org.joda.time.LocalDate

class CalendarGymEntriesViewModel : ViewModel() {
    companion object {
        const val WEIGHT_HISTORY_LIMIT = 7
    }

    private var weightEntryLiveData: LiveData<List<WeightEntry>>? = null
    private var cardioEntryLiveData: LiveData<List<CardioEntry>>? = null

    private val dateFilterLiveData: MutableLiveData<LocalDate> = MutableLiveData()

    fun getGymEntriesForDate(): MediatorLiveData<List<GymEntry>> {
        if (weightEntryLiveData == null) {
            weightEntryLiveData = Transformations.switchMap(dateFilterLiveData) { date ->
                GymBuddyRoomDataBase.weightEntryDao.getAll(date)
            }
        }
        if (cardioEntryLiveData == null) {
            cardioEntryLiveData = Transformations.switchMap(dateFilterLiveData) { date ->
                GymBuddyRoomDataBase.cardioEntryDao.getAll(date)
            }
        }


        return MediatorLiveData<List<GymEntry>>().apply {
            var weightEntries: List<WeightEntry>? = null
            var cardioEntries: List<CardioEntry>? = null

            fun update() {
                val localWeightEntries = weightEntries
                val localCardioEntries = cardioEntries

                if (localWeightEntries != null && localCardioEntries != null) {
                    val gymEntries = mutableListOf<GymEntry>()
                    gymEntries.addAll(localWeightEntries)
                    gymEntries.addAll(localCardioEntries)
                    this.value = gymEntries
                }
            }

            addSource(weightEntryLiveData!!) {
                weightEntries = it
                update()
            }
            addSource(cardioEntryLiveData!!) {
                cardioEntries = it
                update()
            }
        }
    }

    fun getWeightEntryHistoryForDate(date: LocalDate): List<WeightEntry> {
        return GymBuddyRoomDataBase.weightEntryDao.getHistory(date, WEIGHT_HISTORY_LIMIT) ?: listOf()
    }

    fun updateAll(vararg weightEntries: WeightEntry) {
        GymBuddyRoomDataBase.weightEntryDao.updateAll(*weightEntries)
    }

    fun deleteAll(vararg weightEntries: WeightEntry) {
        GymBuddyRoomDataBase.weightEntryDao.deleteAll(*weightEntries)
    }

    fun getWeightEntry(id: Long): WeightEntry? {
        return GymBuddyRoomDataBase.weightEntryDao.get(id)
    }

    fun setDateFilter(date: LocalDate?) {
        dateFilterLiveData.value = date
    }
}