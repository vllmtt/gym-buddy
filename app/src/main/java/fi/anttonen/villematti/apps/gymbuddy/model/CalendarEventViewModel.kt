/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.*
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthWorkoutEntry
import org.joda.time.LocalDate

class CalendarEventViewModel : ViewModel() {
    companion object {
        const val DAYS_TO_FETCH_PRIOR_TO_START_DATE_FILTER = 32
        const val DAYS_TO_FETCH_AFTER_START_DATE_FILTER = 32
    }

    private var weightEntryLiveData: LiveData<List<WeightEntry>>? = null
    private var cardioEntryLiveData: LiveData<List<CardioEntry>>? = null
    private var strengthWorkoutLiveData: LiveData<List<StrengthWorkoutEntry>>? = null

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
        if (strengthWorkoutLiveData == null) {
            strengthWorkoutLiveData = Transformations.switchMap(startDateFilterLiveData) { startDate ->
                GymBuddyRoomDataBase.strengthWorkoutEntryDao.getAll(startDate.plusDays(DAYS_TO_FETCH_AFTER_START_DATE_FILTER), startDate.minusDays(DAYS_TO_FETCH_PRIOR_TO_START_DATE_FILTER))
            }
        }


        return MediatorLiveData<List<GymEntry>>().apply {
            var weightEntries: List<WeightEntry>? = null
            var cardioEntries: List<CardioEntry>? = null
            var strengthWorkoutEntries: List<StrengthWorkoutEntry>? = null

            fun update() {
                val localWeightEntries = weightEntries
                val localCardioEntries = cardioEntries
                val localStrengthWorkoutEntries = strengthWorkoutEntries

                if (localWeightEntries != null && localCardioEntries != null && localStrengthWorkoutEntries != null) {
                    val gymEntries = mutableListOf<GymEntry>()
                    gymEntries.addAll(localWeightEntries)
                    gymEntries.addAll(localCardioEntries)
                    gymEntries.addAll(localStrengthWorkoutEntries)
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
            addSource(strengthWorkoutLiveData!!) {
                strengthWorkoutEntries = it
                update()
            }
        }
    }

    fun setDateFilter(date: LocalDate?) {
        startDateFilterLiveData.value = date
    }
}