/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.content.Context
import org.joda.time.LocalDate
import java.text.DateFormat

/**
 * Created by vma on 25/04/2018.
 */
interface GymEntry : Cloneable {
    fun getHumanReadableDate(context: Context): String {
        //TODO
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(getEntryDate().toDate())
    }

    fun getEntryId(): Long
    fun getEntryDate(): LocalDate
    fun getEntryType(): EntryType
    fun getEntryMood(): String?

    fun updateValuesFrom(entry: GymEntry)
    override fun equals(other: Any?): Boolean
    public override fun clone(): GymEntry
}

enum class EntryType(val displayName: String) {
    WEIGHT("Weight"),
    CARDIO("Cardio"),
    STRENGTH("Gym workout"),
    MEASUREMENT("Measurement"),
    FOOD("Food")
}