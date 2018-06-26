/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.database

import android.arch.persistence.room.TypeConverter
import fi.anttonen.villematti.apps.gymbuddy.model.entity.*
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
    fun fromDuration(value: String?): Duration? {
        return if (value == null) null else Duration.parse(value)
    }

    @TypeConverter
    fun durationToString(duration: Duration?): String? {
        return duration?.toString()
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

    // List<StrengthExerciseSubCharacteristic>

    @TypeConverter
    fun toStrengthExerciseSubCharacteristic(value: String): List<StrengthExerciseSubCharacteristic> {
        return StrengthExercise.parseStrengthExerciseSubCharacteristics(value)
    }

    @TypeConverter
    fun fromStrengthExerciseSubCharacteristic(characteristics: List<StrengthExerciseSubCharacteristic>): String {
        return StrengthExercise.strengthExerciseSubCharacteristicsToDbString(characteristics)
    }

    // StrengthExerciseType

    @TypeConverter
    fun toStrengthExerciseType(value: String): StrengthExerciseType {
        return StrengthExercise.parseStrengthExerciseType(value)
    }

    @TypeConverter
    fun fromStrengthExerciseType(strengthExerciseType: StrengthExerciseType): String {
        return StrengthExercise.strengthExerciseTypeToDbString(strengthExerciseType)
    }

    // MutableList<ExerciseSet>

    @TypeConverter
    fun toExerciseSets(value: String): MutableList<ExerciseSet> {
        return ExerciseSet.parseExerciseSets(value)
    }

    @TypeConverter
    fun fromExerciseSets(sets: MutableList<ExerciseSet>): String {
        return ExerciseSet.toDbString(sets)
    }
}