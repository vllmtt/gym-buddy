package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import fi.anttonen.villematti.apps.gymbuddy.WeightEntry
import java.util.*

/**
 * Created by vma on 25/04/2018.
 */
interface GymEntry : Cloneable {
    fun getEntryId(): String
    fun getEntryDate(): Date
    fun getEntryType(): EntryType
    fun getEntryMood(): String?

    fun updateValuesFrom(entry: GymEntry)
    override fun equals(other: Any?): Boolean
    public override fun clone(): WeightEntry
}

enum class EntryType(val displayName: String) {
    WEIGHT("Weight"),
    CARDIO("Cardio"),
    STRENGTH("Strength"),
}