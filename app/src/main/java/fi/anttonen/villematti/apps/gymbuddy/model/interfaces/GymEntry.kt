package fi.anttonen.villematti.apps.gymbuddy.model.interfaces

import fi.anttonen.villematti.apps.gymbuddy.R.string.date
import fi.anttonen.villematti.apps.gymbuddy.WeightEntry
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vma on 25/04/2018.
 */
interface GymEntry : Cloneable {
    fun getHumanReadableDate(): String {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance()
        target.time = getEntryDate()

        if (now.get(Calendar.YEAR) == target.get(Calendar.YEAR)) {
            if (now.get(Calendar.MONTH) == target.get(Calendar.MONTH)) {
                if (now.get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH)) {
                    return "Today"
                }
                if (now.get(Calendar.DAY_OF_MONTH) - 1 == target.get(Calendar.DAY_OF_MONTH)) {
                    return "Yesterday"
                }
                if (now.get(Calendar.DAY_OF_MONTH) - target.get(Calendar.DAY_OF_MONTH) < 7) {
                    val weekday = when (target.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> "Monday"
                        Calendar.TUESDAY -> "Tuesday"
                        Calendar.WEDNESDAY -> "Wednesday"
                        Calendar.THURSDAY -> "Thursday"
                        Calendar.FRIDAY -> "Friday"
                        Calendar.SATURDAY -> "Saturday"
                        Calendar.SUNDAY -> "Sunday"
                        else -> ""
                    }
                    return weekday
                }
            }

        }
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(target.time)
    }

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