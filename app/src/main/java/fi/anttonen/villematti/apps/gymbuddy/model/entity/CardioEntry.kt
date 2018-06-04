package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import fi.anttonen.villematti.apps.gymbuddy.R.string.*
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager.Units.distanceRatio
import fi.anttonen.villematti.apps.gymbuddy.misc.format
import fi.anttonen.villematti.apps.gymbuddy.misc.roundToDecimalPlaces
import net.danlew.android.joda.JodaTimeAndroid.init
import org.joda.time.Duration
import org.joda.time.LocalDate

@Entity(tableName = "cardio_entry")
class CardioEntry(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
                  @ColumnInfo(name = "date") var date: LocalDate) : GymEntry {

    @ColumnInfo(name = "distance")
    private var _distance: Double? = null

    @ColumnInfo(name = "duration")
    var duration: Duration? = null

    @ColumnInfo(name = "cardio_type")
    var cardioType: CardioType? = null

    @ColumnInfo(name = "mood")
    var mood: String? = null

    fun getDistance(): Double? {
        return _distance
    }

    fun getDistanceUI(decimals: Int): Double? {
        return ((_distance ?: 0.0) * UnitManager.Units.distanceRatio).roundToDecimalPlaces(decimals)
    }

    fun setDistance(value: Double?) {
        setDistance(value, false)
    }

    fun setDistance(value: Double?, adjustToDb: Boolean) {
        _distance = if (adjustToDb) {
            (value ?: 0.0) / UnitManager.Units.distanceRatio
        } else {
            value
        }
    }

    fun avgSpeed(): Double? {
        val dist = getDistanceUI(3)
        val dur = duration
        if (dist != null && dur != null) {
            val hours = dur.millis.toDouble() / (1000 * 60 * 60).toDouble()
            return dist / hours
        }
        return null
    }

    fun avgSpeedText(): String? {
        val avg = avgSpeed()
        return if (avg == null) null else "${avg.roundToDecimalPlaces(2)}${getMainDistanceUnitString()}/h"
    }

    override fun getEntryType(): EntryType = EntryType.CARDIO

    override fun getEntryId(): Long = id

    override fun getEntryDate(): LocalDate = date

    override fun getEntryMood(): String? = mood

    override fun updateValuesFrom(entry: GymEntry) {
        if (entry is CardioEntry) {
            date = entry.date
            this.setDistance(entry.getDistance(), false)
            duration = entry.duration
            mood = entry.mood
            cardioType = entry.cardioType
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is CardioEntry) {
            return other.id == this.id && other.date == this.date && other.getDistance() == this.getDistance() && other.duration == this.duration && other.mood == this.mood
        }
        return false
    }

    override fun clone(): GymEntry {
        val clone = CardioEntry(this.id, LocalDate(this.date))
        clone.setDistance(getDistance(), false)
        clone.duration = this.duration
        clone.mood = this.mood
        clone.cardioType = this.cardioType
        return clone
    }


    fun getHumanReadableDuration(): String? {
        if (duration == null) return null

        val dur = duration ?: Duration.ZERO

        val d = dur.standardDays
        val h = dur.standardHours - d * 24
        val m = dur.standardMinutes - d * 24 * 60 - h * 60
        val s = dur.standardSeconds - d * 24 * 60 - h * 60 * 60 - m * 60
        //val ms = duration.millis - s * 1000


        /*
        if (d > 0) sb.append("$d${getDayUnitString()} ")
        if (d > 0 || h > 0) sb.append("$h${getHourUnitString()} ")
        if (d > 0 || h > 0 || m > 0) sb.append("$m${getMinuteUnitString()} ")
        sb.append("$s${getSecondsUnitString()}")
        */
        return "${h.format(2)}:${m.format(2)}:${s.format(2)}"
    }


    fun getHumanReadableDistance(): String? {
        val dist = getDistanceUI(3)
        if (dist != null && dist > 0.0) {
            return "$dist${getMainDistanceUnitString()}"
        }

        return null
    }

    companion object {
        fun getDayUnitString() = "d"
        fun getHourUnitString() = "h"
        fun getMinuteUnitString() = "min"
        fun getSecondsUnitString() = "s"

        fun getMainDistanceUnitString(): String {
            return when (UnitManager.Units.distanceRatio) {
                UnitManager.DistanceRatio.KM -> "km"
                UnitManager.DistanceRatio.M -> "mi"
                else -> "unknown"
            }
        }

        fun getSecondaryDistanceUnitString(): String {
            return when (UnitManager.Units.distanceRatio) {
                UnitManager.DistanceRatio.KM -> "m"
                UnitManager.DistanceRatio.M -> "ft"
                else -> "unknown"
            }
        }
    }
}