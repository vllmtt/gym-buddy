package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import fi.anttonen.villematti.apps.gymbuddy.R.string.distance
import fi.anttonen.villematti.apps.gymbuddy.R.string.mood
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager.Units.distanceRatio
import fi.anttonen.villematti.apps.gymbuddy.misc.roundToDecimalPlaces
import org.joda.time.Duration
import org.joda.time.LocalDate

@Entity(tableName = "cardio_entry")
class CardioEntry(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
                  @ColumnInfo(name = "date") var date: LocalDate,
                  @ColumnInfo(name = "distance") private var _distance: Double,
                  @ColumnInfo(name = "duration") var duration: Duration)  : GymEntry {

    var distance: Double
    get() = (_distance * UnitManager.Units.distanceRatio).roundToDecimalPlaces(1)
    set(value) {
        _distance = value / UnitManager.Units.distanceRatio
    }

    @ColumnInfo(name = "mood")
    var mood: String? = null

    /**
     * Save distance to database as m regardless of user preference
     */
    init {
        _distance /= UnitManager.Units.distanceRatio
    }

    override fun getEntryType(): EntryType = EntryType.CARDIO

    override fun getEntryId(): Long = id

    override fun getEntryDate(): LocalDate = date

    override fun getEntryMood(): String? = mood

    override fun updateValuesFrom(entry: GymEntry) {
        if (entry is CardioEntry) {
            date = entry.date
            distance = entry.distance
            duration = entry.duration
            mood = entry.mood
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is CardioEntry) {
            return other.id == this.id && other.date == this.date && other.distance == this.distance && other.duration == this.duration && other.mood == this.mood
        }
        return false
    }

    override fun clone(): GymEntry {
        val clone = CardioEntry(this.id, LocalDate(this.date), this.distance, this.duration)
        clone.mood = this.mood
        return clone
    }

    fun getDayUnitString() = "d"
    fun getHourUnitString() = "h"
    fun getMinuteUnitString() = "min"
    fun getSecondsUnitString() = "s"

    fun getHumanReadableDuration(): String {
        val d = duration.standardDays
        val h = duration.standardHours - d * 24
        val m = duration.standardMinutes - d * 24 * 60 - h * 60
        val s = duration.standardSeconds - d * 24 * 60 - h * 60 * 60 - m * 60
        //val ms = duration.millis - s * 1000

        val sb = StringBuilder()
        if (d > 0) sb.append("$d${getDayUnitString()} ")
        if (d > 0 || h > 0) sb.append("$h${getHourUnitString()} ")
        if (d > 0 || h > 0 || m > 0) sb.append("$m${getMinuteUnitString()} ")
        sb.append("$s${getSecondsUnitString()}")

        return sb.toString()
    }

    fun getMainDistanceUnitString(): String {
        return when (UnitManager.Units.distanceRatio) {
            UnitManager.DistanceRatio.KM -> "km"
            UnitManager.DistanceRatio.M ->  "mi"
            else -> "unknown"
        }
    }

    fun getSecondaryDistanceUnitString(): String {
        return when (UnitManager.Units.distanceRatio) {
            UnitManager.DistanceRatio.KM -> "m"
            UnitManager.DistanceRatio.M ->  "ft"
            else -> "unknown"
        }
    }

    fun getHumanReadableDistance(): String {
        return "$distance${getSecondaryDistanceUnitString()}"
    }

}