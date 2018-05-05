package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import org.joda.time.LocalDate

class CalendarGymEntriesViewModel : ViewModel() {
    private var gymEntryLiveData: LiveData<List<WeightEntry>>? = null

    fun getWeightEntries(date: LocalDate, dao: GymEntryDao): LiveData<List<WeightEntry>> {
        if (gymEntryLiveData == null) {
            gymEntryLiveData = dao.getAll(date)
        }
        return gymEntryLiveData!!
    }
}