/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioType

class CardioWorkoutViewModel : ViewModel() {

    fun getAllExercises(sortByUsageCount: Boolean): LiveData<List<CardioType>> {
        if (sortByUsageCount) {
            return GymBuddyRoomDataBase.cardioTypeDao.getAllSortedByUsageCount()
        }
        return GymBuddyRoomDataBase.cardioTypeDao.getAll()
    }

    fun getExercise(id: Long): CardioType? {
        return GymBuddyRoomDataBase.cardioTypeDao.get(id)
    }
}