package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import fi.anttonen.villematti.apps.gymbuddy.R.string.date
import fi.anttonen.villematti.apps.gymbuddy.model.WeightEntry
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Instant
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
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
    public override fun clone(): WeightEntry
}

enum class EntryType(val displayName: String) {
    WEIGHT("Weight"),
    CARDIO("Cardio"),
    STRENGTH("Strength"),
    MEASUREMENT("Measurement"),
    FOOD("Food")
}