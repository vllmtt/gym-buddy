/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.*
import org.joda.time.LocalDate

@Entity(tableName = "strength_workout_entry")
class StrengthWorkoutEntry(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
                           @ColumnInfo(name = "date") var date: LocalDate) : GymEntry {

    @ColumnInfo(name = "mood")
    var mood: String? = null

    //@Relation(parentColumn = "id", entityColumn = "workoutId", entity = ExerciseSet::class)
    var sets = mutableListOf<ExerciseSet>()

    @Ignore
    private var exerciseSequenceMap: MutableMap<Int, MutableList<ExerciseSet>> = mutableMapOf()

    fun getExerciseSequenceMap(): MutableMap<Int, MutableList<ExerciseSet>> {
        exerciseSequenceMap = mutableMapOf()
        for (set in sets) {
            val exerciseSequence = set.exerciseSequence
            if (!exerciseSequenceMap.containsKey(exerciseSequence)) {
                val filtered = sets.filter { it.exerciseSequence == exerciseSequence }
                exerciseSequenceMap[exerciseSequence] = mutableListOf(*filtered.toTypedArray())
            }
        }
        return exerciseSequenceMap
    }

    override fun getEntryId(): Long = id
    override fun getEntryDate(): LocalDate = date
    override fun getEntryType(): EntryType = EntryType.STRENGTH
    override fun getEntryMood(): String? = mood

    override fun updateValuesFrom(entry: GymEntry) {
        if (entry is StrengthWorkoutEntry) {
            this.date = entry.date
            this.mood = entry.mood

            this.sets.clear()
            this.sets.addAll(entry.sets)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is StrengthWorkoutEntry) {
            return other.id == this.id && other.date == this.date && other.mood == this.mood && isSameSets(other.sets)
        }
        return false
    }

    private fun isSameSets(otherSets: List<ExerciseSet>): Boolean {
        otherSetsLoop@for (otherSet in otherSets) {
            for (thisSet in sets) {
                if (thisSet == otherSet) continue@otherSetsLoop // Match found from this.sets
            }
            // No match found from this.sets
            return false
        }
        return true
    }

    override fun clone(): GymEntry {
        val clone = StrengthWorkoutEntry(0, LocalDate(this.date))
        clone.mood = this.mood
        clone.sets.addAll(cloneSets(this.sets))
        return clone
    }

    private fun cloneSets(sets: List<ExerciseSet>): List<ExerciseSet> {
        val clones = mutableListOf<ExerciseSet>()
        for (set in sets) {
            val clone = ExerciseSet(set.id, set.workoutId, set.exerciseId, set.setSequence, set.exerciseSequence)
            clone.reps = set.reps
            clone.setWeight(set.getWeight())
            clone.workingSet = set.workingSet
            clones.add(clone)
        }
        return clones
    }
}