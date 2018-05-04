package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.persistence.room.TypeConverter
import org.joda.time.LocalDate

class Converters {

    @TypeConverter
    fun fromTimestamp(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate): String {
        return date.toString()
    }
}