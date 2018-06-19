/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.misc

import android.os.AsyncTask
import android.util.Log
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import fi.anttonen.villematti.apps.gymbuddy.model.entity.ExerciseSet
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthExercise
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthWorkoutEntry

class WorkoutCoordinator(val workout: StrengthWorkoutEntry) {
    // Map of which sets belong to each exercise sequence
    var sequenceSetsMap: MutableMap<Int, MutableList<ExerciseSet>> = workout.getExerciseSequenceMap()

    // Map of which exercise id corresponds to each exercise sequence
    var sequenceIdMap: MutableMap<Int, Long> = mutableMapOf()

    // Map of how much each exercise's usage count should be incremented
    private var usageCountMap: MutableMap<Long, Int> = mutableMapOf()

    // Highest currently used exercise and set sequence
    private var lastExerciseSequence: Int = -1
    private var lastSetSequence: Int = -1

    init {
        computeLastExerciseSequence()
        computeLastSetSequence()
        computeSequenceIdMap()
    }

    fun addExercise(exerciseId: Long): Int {
        lastExerciseSequence++
        sequenceSetsMap[lastExerciseSequence] = mutableListOf()
        sequenceIdMap[lastExerciseSequence] = exerciseId
        updateUsageCount(exerciseId, 1, 1)
        return lastExerciseSequence
    }

    fun removeExercise(exerciseSequence: Int, exerciseId: Long) {
        sequenceSetsMap.remove(exerciseSequence)
        sequenceIdMap.remove(exerciseSequence)
        updateUsageCount(exerciseId, -1)
        if (exerciseSequence == lastExerciseSequence) computeLastExerciseSequence()
    }

    fun addSet(set: ExerciseSet) {
        lastSetSequence++
        set.setSequence = lastSetSequence
        val sets = sequenceSetsMap[set.exerciseSequence]!!
        sets.add(set)
        sets.sort()
    }

    fun removeSet(set: ExerciseSet) {
        sequenceSetsMap[set.exerciseSequence]!!.remove(set)
        if (set.setSequence == lastSetSequence) computeLastSetSequence()
    }

    fun swap(firstExerciseSequence: Int?, secondExerciseSequence: Int?) {
        if (firstExerciseSequence != null && secondExerciseSequence != null) {
            val firstSets = sequenceSetsMap[firstExerciseSequence]
            val secondSets = sequenceSetsMap[secondExerciseSequence]
            firstSets!!.forEach { it.exerciseSequence = secondExerciseSequence }
            secondSets!!.forEach { it.exerciseSequence = firstExerciseSequence }
            sequenceSetsMap[firstExerciseSequence] = secondSets
            sequenceSetsMap[secondExerciseSequence] = firstSets
        } else {
            Log.e("WorkoutCoordinator", "Couldn't swap exercises, sequence provided was null")
        }
    }

    fun saveUsageCounts() {
        AsyncTask.execute {
            val exercises = mutableListOf<StrengthExercise>()
            for (usageEntry in usageCountMap) {
                val exercise = GymBuddyRoomDataBase.strengthExerciseDao.get(usageEntry.key)
                exercise.usageCount += usageEntry.value
                exercises.add(exercise)
            }
            GymBuddyRoomDataBase.strengthExerciseDao.updateAll(*exercises.toTypedArray())
        }
    }

    private fun updateUsageCount(exerciseId: Long, change: Int, valueIfAbsent: Int? = null) {
        if (usageCountMap.containsKey(exerciseId)) {
            val previousCount = usageCountMap[exerciseId]!!
            usageCountMap[exerciseId] = previousCount + change
        } else {
            if (valueIfAbsent != null) usageCountMap[exerciseId] = valueIfAbsent
        }
    }

    private fun computeLastExerciseSequence() {
        for (exerciseSequence in sequenceSetsMap.keys) {
            if (exerciseSequence > lastExerciseSequence) {
                lastExerciseSequence = exerciseSequence
            }
        }
    }

    private fun computeLastSetSequence() {
        for (set in workout.sets) {
            if (set.setSequence > lastSetSequence) {
                lastSetSequence = set.setSequence
            }
        }
    }

    private fun computeSequenceIdMap() {
        for (sets in sequenceSetsMap.values) {
            val set = sets.first() // Every exercise must have at least one set
            sequenceIdMap.put(set.exerciseSequence, set.exerciseId)
        }
    }
}