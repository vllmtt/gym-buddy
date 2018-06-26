/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthExercise

class StrengthWorkoutViewModel : ViewModel() {
    private var exercisesLiveData: LiveData<List<StrengthExercise>>? = null

    fun getAllExercises(sortByUsageCount: Boolean): LiveData<List<StrengthExercise>> {
        if (sortByUsageCount) {
            return GymBuddyRoomDataBase.strengthExerciseDao.getAllSortedByUsageCount()
        }
        return GymBuddyRoomDataBase.strengthExerciseDao.getAll()
    }

    fun getExercise(id: Long): StrengthExercise? {
        return GymBuddyRoomDataBase.strengthExerciseDao.get(id)
    }
}