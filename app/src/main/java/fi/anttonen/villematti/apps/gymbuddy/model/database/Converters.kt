package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.persistence.room.TypeConverter
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioType
import org.joda.time.Duration
import org.joda.time.LocalDate

class Converters {

    // LocalDate

    @TypeConverter
    fun fromTimestamp(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate): String {
        return date.toString()
    }

    // Duration

    @TypeConverter
    fun fromDuration(value: String): Duration {
        return Duration.parse(value)
    }

    @TypeConverter
    fun durationToString(duration: Duration): String {
        return duration.toString()
    }

    // CardioType

    @TypeConverter
    fun fromCardioType(value: String): CardioType {
        return CardioType.parse(value)
    }

    @TypeConverter
    fun cardioTypeToString(cardioType: CardioType): String {
        return cardioType.toDbString()
    }
}