/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.entity

import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.misc.roundToDecimalPlaces

class ExerciseSet(val id: Long,
                  val workoutId: Long,
                  val exerciseId: Long,
                  var setSequence: Int,
                  var exerciseSequence: Int) : Comparable<ExerciseSet> {

    var workingSet: Boolean = false
    var reps: Int? = null
    private var weight: Double? = null

    fun getWeightUI(decimals: Int): Double? {
        return if (weight == null) null else (weight ?: 0.0 * UnitManager.Units.weightRatio).roundToDecimalPlaces(decimals)
    }

    fun getWeight(): Double? {
        return weight
    }

    fun setWeight(value: Double?) {
        setWeight(value, false)
    }

    fun setWeight(value: Double?, adjustToDb: Boolean) {
        weight = if (value != null && adjustToDb) {
            value / UnitManager.Units.weightRatio
        } else {
            value
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is ExerciseSet &&
                other.id == this.id &&
                other.exerciseId == this.exerciseId &&
                other.workoutId == this.workoutId &&
                other.workingSet == this.workingSet &&
                other.reps == this.reps &&
                other.weight == this.weight &&
                other.setSequence == this.setSequence &&
                other.exerciseSequence == this.exerciseSequence
    }

    override fun compareTo(other: ExerciseSet): Int {
        val exerciseSequenceComparison = exerciseSequence - other.exerciseSequence
        return if (exerciseSequenceComparison == 0) setSequence - other.setSequence else exerciseSequenceComparison
    }

    companion object {
        fun parseExerciseSets(value: String): List<ExerciseSet> {
            val valueStrings = value.split("|")
            val sets = mutableListOf<ExerciseSet>()
            for (valueString in valueStrings) {
                sets.add(ExerciseSet.parseExerciseSet(valueString))
            }
            return sets
        }

        fun toDbString(sets: List<ExerciseSet>): String {
            val sb = StringBuilder()
            for (set in sets) {
                sb.append(ExerciseSet.toDbString(set))
                sb.append("|")
            }
            return sb.toString()
        }

        fun parseExerciseSet(value: String): ExerciseSet {
            val valueString = value.split(";")

            val id = valueString[0].toLong()
            val exerciseId = valueString[1].toLong()
            val workoutId = valueString[2].toLong()
            val sequence = valueString[3].toInt()
            val exerciseSequence = valueString[4].toInt()
            val workingSet = valueString[5] == "1"
            val reps = if (valueString[6].isEmpty()) null else valueString[6].toInt()
            val weight = if (valueString[7].isEmpty()) null else valueString[7].toDouble()

            val set = ExerciseSet(id, workoutId, exerciseId, sequence, exerciseSequence)
            set.setWeight(weight)
            set.workingSet = workingSet
            set.reps = reps

            return set
        }

        fun toDbString(set: ExerciseSet): String {
            val workingSetString = if (set.workingSet) "1" else "0"
            val repsString = if (set.reps != null) set.reps.toString() else ""
            val weightString = if (set.getWeight() != null) set.getWeight().toString() else ""
            return "${set.id};${set.workoutId};${set.exerciseId};${set.setSequence};${set.exerciseSequence};$workingSetString;$repsString;$weightString"
        }
    }
}
