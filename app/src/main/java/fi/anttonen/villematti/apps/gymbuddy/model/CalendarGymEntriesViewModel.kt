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

    private var historyWeightEntryData: List<WeightEntry>? = null

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
            Log.i("LIVE DATA", "Marking everything null")

            fun update() {
                Log.i("LIVE DATA", "Entering update")
                val localWeightEntries = weightEntries
                val localCardioEntries = cardioEntries

                if (localWeightEntries != null && localCardioEntries != null) {
                    val gymEntries = mutableListOf<GymEntry>()
                    gymEntries.addAll(localWeightEntries)
                    gymEntries.addAll(localCardioEntries)
                    this.value = gymEntries
                    Log.i("LIVE DATA", "All done!")
                }
            }

            addSource(weightEntryLiveData!!) {
                weightEntries = it
                Log.i("LIVE DATA", "Going to update from weight entry")
                update()
            }
            addSource(cardioEntryLiveData!!) {
                cardioEntries = it
                Log.i("LIVE DATA", "Going to update from cardio entry")
                update()
            }
        }
    }

    fun getWeightEntryHistoryForDate(date: LocalDate): List<WeightEntry> {
        if (historyWeightEntryData == null) {
            historyWeightEntryData = GymBuddyRoomDataBase.weightEntryDao.getHistory(date, WEIGHT_HISTORY_LIMIT)
        }
        return historyWeightEntryData!!
    }

    fun setDateFilter(date: LocalDate?) {
        dateFilterLiveData.value = date
        historyWeightEntryData = null
    }
}