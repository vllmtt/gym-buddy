package fi.anttonen.villematti.apps.gymbuddy.model

import android.arch.persistence.room.TypeConverter
import org.joda.time.Duration
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

    @TypeConverter
    fun fromDuration(value: String): Duration {
        return Duration.parse(value)
    }

    @TypeConverter
    fun durationToString(duration: Duration): String {
        return duration.toString()
    }
}