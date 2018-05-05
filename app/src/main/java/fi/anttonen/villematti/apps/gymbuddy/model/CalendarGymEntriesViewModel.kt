package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymBuddyRoomDataBase
import org.joda.time.LocalDate

class CalendarGymEntriesViewModel : ViewModel() {
    private var gymEntryLiveData: LiveData<List<WeightEntry>>? = null
    private val dateLiveData: MutableLiveData<LocalDate> = MutableLiveData()

    fun getWeightEntriesForDate(): LiveData<List<WeightEntry>> {
        if (gymEntryLiveData == null) {
            gymEntryLiveData = Transformations.switchMap(dateLiveData) { date ->
                GymBuddyRoomDataBase.gymEntryDao.getAll(date)
            }
        }
        return gymEntryLiveData!!
    }

    fun setDateFilter(date: LocalDate?) {
        dateLiveData.value = date
    }
}