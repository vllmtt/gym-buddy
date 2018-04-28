package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import java.util.*

/**
 * Created by vma on 25/04/2018.
 */
interface GymEntry {
    fun getEntryId(): String
    fun getEntryDate(): Date
    fun getEntryType(): EntryType
}

enum class EntryType(val displayName: String) {
    WEIGHT("Weight"),
    CARDIO("Cardio"),
    STRENGTH("Strength"),
}