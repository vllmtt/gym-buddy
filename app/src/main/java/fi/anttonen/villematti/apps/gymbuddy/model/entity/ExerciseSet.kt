/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.misc.roundToDecimalPlaces

//@Entity(tableName = "exercise_set")
class ExerciseSet(/*@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true)*/ val id: Long,
                  /*@ColumnInfo(name = "workoutId")*/ val workoutId: Long,
                  /*@ColumnInfo(name = "exerciseId")*/ val exerciseId: Long,
                  /*@ColumnInfo(name = "sequence")*/ var sequence: Int) : Comparable<ExerciseSet> {

    //@ColumnInfo(name = "working_set")
    var workingSet: Boolean = false

    //@ColumnInfo(name = "reps")
    var reps: Int? = null

    //@ColumnInfo(name = "weight")
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
                other.sequence == this.sequence
    }

    override fun compareTo(other: ExerciseSet): Int {
        return sequence - other.sequence
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
            val workingSet = valueString[4] == "1"
            val reps = if (valueString[5].isEmpty()) null else valueString[5].toInt()
            val weight = if (valueString[6].isEmpty()) null else valueString[6].toDouble()

            val set = ExerciseSet(id, workoutId, exerciseId, sequence)
            set.setWeight(weight)
            set.workingSet = workingSet
            set.reps = reps

            return set
        }

        fun toDbString(set: ExerciseSet): String {
            val workingSetString = if (set.workingSet) "1" else "0"
            val repsString = if (set.reps != null) set.reps.toString() else ""
            val weightString = if (set.getWeight() != null) set.getWeight().toString() else ""
            return "${set.id};${set.workoutId};${set.exerciseId};${set.sequence};$workingSetString;$repsString;$weightString"
        }
    }
}
