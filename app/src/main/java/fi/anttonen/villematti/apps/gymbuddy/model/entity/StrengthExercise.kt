/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import fi.anttonen.villematti.apps.gymbuddy.misc.SearchableView
import fi.anttonen.villematti.apps.gymbuddy.misc.SearchableViewType

@Entity(tableName = "strength_exercise")
class StrengthExercise(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
                       @ColumnInfo var name: String,
                       @ColumnInfo var type: StrengthExerciseType) : SearchableView {

    @ColumnInfo
    var subCharacteristics = mutableListOf<StrengthExerciseSubCharacteristic>()

    @ColumnInfo
    var usageCount: Long = 0

    override fun matches(query: String?): Boolean {
        return if (query.isNullOrEmpty()) true else name.contains(query!!) || type.description.contains(query)
    }

    override fun title() = name
    override fun subTitle() = type.description
    override fun meta(): String {
        return when (usageCount) {
            0L -> ""
            1L -> "$usageCount time used"
            else -> "$usageCount times used"
        }
    }
    override fun relevanceCount() = usageCount
    override fun type() = SearchableViewType.CONTENT

    companion object {
        fun create(name: String, type: StrengthExerciseType, vararg subCharacteristics: StrengthExerciseSubCharacteristic): StrengthExercise {
            val exercise = StrengthExercise(0, name, type)
            exercise.subCharacteristics.addAll(subCharacteristics)
            return exercise
        }

        fun strengthExerciseTypeToDbString(strengthExerciseType: StrengthExerciseType): String {
            return strengthExerciseType.toString()
        }

        fun parseStrengthExerciseType(value: String): StrengthExerciseType {
            return StrengthExerciseType.valueOf(value)
        }

        fun strengthExerciseSubCharacteristicToDbString(characteristic: StrengthExerciseSubCharacteristic): String {
            return characteristic.toString()
        }

        fun parseStrengthExerciseSubCharacteristic(value: String): StrengthExerciseSubCharacteristic {
            return StrengthExerciseSubCharacteristic.valueOf(value)
        }

        fun strengthExerciseSubCharacteristicsToDbString(characteristics: List<StrengthExerciseSubCharacteristic>): String {
            return characteristics.joinToString(";", "", "")
        }

        fun parseStrengthExerciseSubCharacteristics(value: String): List<StrengthExerciseSubCharacteristic> {
            val stringValues = value.split(";")
            val characteristics = mutableListOf<StrengthExerciseSubCharacteristic>()
            for (stringValue in stringValues) {
                if (stringValue.isNotEmpty()) {
                    characteristics.add(StrengthExerciseSubCharacteristic.valueOf(stringValue))
                }
            }
            return characteristics
        }

        val DEFAULT_EXERCISES = mutableListOf<StrengthExercise>().apply {
            this.add(StrengthExercise.create("Ab crunch", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Ab crunch", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Ab crunch", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Back extension", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Back extension", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Bench press", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Bench press", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Bench press", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Bicep curl", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Bicep curl", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Bicep curl", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Bicep curl", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Rope bicep curl", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Rope hammer curl", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Hammer curl", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Incline bicep curl", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Deadlift", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Front raise", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Front raise", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Front raise", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Incline bench press", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Incline bench press", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Incline bench press", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Lunge", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Lunge", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Shrug", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Shrug", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Back squat", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Back squat", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Bent over row", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Rope rear delt row", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Triceps extension", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Triceps extension", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Triceps extension", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Rope triceps extension", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Triceps pushdown", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Chin-up", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Chin-up", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Clean", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Concentration curl", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Dips", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Dips", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Flyes", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Flyes", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Flyes", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Incline flyes", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Kickbacks", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Chest press", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Pullover", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Pullover", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Rear delt raise", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Rear delt raise", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Shoulder press", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Shoulder press", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Shoulder press", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Face pull", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Farmer's walk", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Front squat", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Hack squat", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Hanging leg raise", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Lat pull down", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Leg extension", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Leg press", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Lying leg curl", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Lying T-bar row", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Muscle up", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Pistol squat", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Plank", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Pullup", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Pullup", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Pushup", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Rack pull", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Seated calf raise", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Seated leg curl", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Seated row", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Side lateral raise", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Side lateral raise", StrengthExerciseType.DUMBBELL))
            this.add(StrengthExercise.create("Sit-ups", StrengthExerciseType.BODY_WEIGHT))
            this.add(StrengthExercise.create("Stiff-legged deadlift", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("Sumo deadlift", StrengthExerciseType.BARBELL))
            this.add(StrengthExercise.create("T-bar row", StrengthExerciseType.MACHINE))
            this.add(StrengthExercise.create("Upright row", StrengthExerciseType.CABLE))
            this.add(StrengthExercise.create("Upright row", StrengthExerciseType.BARBELL))
        }
    }
}

enum class StrengthExerciseType(val description: String) {
    BARBELL("Barbell"),
    BODY_WEIGHT("Body weight"),
    CABLE("Cable"),
    DUMBBELL("Dumbbell"),
    MACHINE("Machine"),
    OTHER("Other");

    override fun toString() = description
}

enum class StrengthExerciseMainCharacteristic {
    UPPER_BODY,
    LOWER_BODY,
    OTHER
}

enum class StrengthExerciseCharacteristic(val mainCharacteristic: StrengthExerciseMainCharacteristic) {
    ARMS(StrengthExerciseMainCharacteristic.UPPER_BODY),
    SHOULDERS(StrengthExerciseMainCharacteristic.UPPER_BODY),
    CHEST(StrengthExerciseMainCharacteristic.UPPER_BODY),
    BACK(StrengthExerciseMainCharacteristic.UPPER_BODY),
    CORE(StrengthExerciseMainCharacteristic.UPPER_BODY),
    LEGS(StrengthExerciseMainCharacteristic.LOWER_BODY),
    OTHER(StrengthExerciseMainCharacteristic.OTHER)
}

enum class StrengthExerciseSubCharacteristic(val characteristic: StrengthExerciseCharacteristic) {
    BICEPS(StrengthExerciseCharacteristic.ARMS),
    TRICEPS(StrengthExerciseCharacteristic.ARMS),

    CHEST(StrengthExerciseCharacteristic.CHEST),

    FRONT_DELTS(StrengthExerciseCharacteristic.SHOULDERS),
    SIDE_DELTS(StrengthExerciseCharacteristic.SHOULDERS),
    REAR_DELTS(StrengthExerciseCharacteristic.SHOULDERS),

    LATS(StrengthExerciseCharacteristic.BACK),
    TRAPS(StrengthExerciseCharacteristic.BACK),
    MID_BACK(StrengthExerciseCharacteristic.BACK),
    LOWER_BACK(StrengthExerciseCharacteristic.BACK),

    ABS(StrengthExerciseCharacteristic.CORE),
    OBLIQUES(StrengthExerciseCharacteristic.CORE),

    QUADS(StrengthExerciseCharacteristic.LEGS),
    HAMSTRINGS(StrengthExerciseCharacteristic.LEGS),
    CALVES(StrengthExerciseCharacteristic.LEGS)
}