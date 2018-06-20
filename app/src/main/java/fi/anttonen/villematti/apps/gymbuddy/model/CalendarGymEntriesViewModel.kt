/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.*
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import fi.anttonen.villematti.apps.gymbuddy.model.entity.*
import org.joda.time.LocalDate

class CalendarGymEntriesViewModel : ViewModel() {
    companion object {
        const val WEIGHT_HISTORY_LIMIT = 7
    }

    private var weightEntryLiveData: LiveData<List<WeightEntry>>? = null
    private var cardioEntryLiveData: LiveData<List<CardioEntry>>? = null
    private var strengthWorkoutLiveData: LiveData<List<StrengthWorkoutEntry>>? = null

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
        if (strengthWorkoutLiveData == null) {
            strengthWorkoutLiveData = Transformations.switchMap(dateFilterLiveData) { date ->
                GymBuddyRoomDataBase.strengthWorkoutEntryDao.getAll(date)
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

    fun getWeightEntryHistoryForDate(date: LocalDate): List<WeightEntry> {
        return GymBuddyRoomDataBase.weightEntryDao.getHistory(date, WEIGHT_HISTORY_LIMIT)
    }

    fun updateAll(vararg weightEntries: WeightEntry) {
        GymBuddyRoomDataBase.weightEntryDao.updateAll(*weightEntries)
    }

    fun updateAll(vararg cardioEntries: CardioEntry) {
        GymBuddyRoomDataBase.cardioEntryDao.updateAll(*cardioEntries)
    }

    fun updateAll(vararg strengthWorkoutEntries: StrengthWorkoutEntry) {
        GymBuddyRoomDataBase.strengthWorkoutEntryDao.updateAll(*strengthWorkoutEntries)
    }

    fun deleteAll(vararg weightEntries: WeightEntry) {
        GymBuddyRoomDataBase.weightEntryDao.deleteAll(*weightEntries)
    }

    fun deleteAll(vararg cardioEntries: CardioEntry) {
        GymBuddyRoomDataBase.cardioEntryDao.deleteAll(*cardioEntries)
    }

    fun deleteAll(vararg strengthWorkoutEntries: StrengthWorkoutEntry) {
        GymBuddyRoomDataBase.strengthWorkoutEntryDao.deleteAll(*strengthWorkoutEntries)
    }

    fun getWeightEntry(id: Long): WeightEntry? {
        return GymBuddyRoomDataBase.weightEntryDao.get(id)
    }

    fun getCardioEntry(id: Long): CardioEntry? {
        return GymBuddyRoomDataBase.cardioEntryDao.get(id)
    }

    fun  getStrengthWorkoutEntry(id: Long): StrengthWorkoutEntry? {
        return GymBuddyRoomDataBase.strengthWorkoutEntryDao.get(id)
    }

    fun getStrengthExercise(id: Long): StrengthExercise {
        return GymBuddyRoomDataBase.strengthExerciseDao.get(id)
    }

    fun setDateFilter(date: LocalDate?) {
        dateFilterLiveData.value = date
    }

}