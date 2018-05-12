package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.*
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import org.joda.time.LocalDate

class CalendarEventViewModel : ViewModel() {
    companion object {
        const val DAYS_TO_FETCH_PRIOR_TO_START_DATE_FILTER = 32
        const val DAYS_TO_FETCH_AFTER_START_DATE_FILTER = 32
    }

    private var weightEntryLiveData: LiveData<List<WeightEntry>>? = null
    private var cardioEntryLiveData: LiveData<List<CardioEntry>>? = null

    private val startDateFilterLiveData: MutableLiveData<LocalDate> = MutableLiveData()

    fun getGymEntriesForDateRange(): MediatorLiveData<List<GymEntry>> {
        if (weightEntryLiveData == null) {
            weightEntryLiveData = Transformations.switchMap(startDateFilterLiveData) { startDate ->
                GymBuddyRoomDataBase.weightEntryDao.getAll(startDate.plusDays(DAYS_TO_FETCH_AFTER_START_DATE_FILTER), startDate.minusDays(DAYS_TO_FETCH_PRIOR_TO_START_DATE_FILTER))
            }
        }
        if (cardioEntryLiveData == null) {
            cardioEntryLiveData = Transformations.switchMap(startDateFilterLiveData) { startDate ->
                GymBuddyRoomDataBase.cardioEntryDao.getAll(startDate.plusDays(DAYS_TO_FETCH_AFTER_START_DATE_FILTER), startDate.minusDays(DAYS_TO_FETCH_PRIOR_TO_START_DATE_FILTER))
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

    fun setDateFilter(date: LocalDate?) {
        startDateFilterLiveData.value = date
    }
}