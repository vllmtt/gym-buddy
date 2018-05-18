package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.util.Log
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import fi.anttonen.villematti.apps.gymbuddy.R.string.date
import fi.anttonen.villematti.apps.gymbuddy.R.string.mood
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.misc.roundToDecimalPlaces
import org.joda.time.LocalDate
import java.math.BigDecimal

/**
 * Created by vma on 25/04/2018.
 */
@Entity(tableName = "weight_entry")
class WeightEntry(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
                  @ColumnInfo(name = "date") var date: LocalDate,
                  @ColumnInfo(name = "weight") private var _weight: Double) : GymEntry {


    @ColumnInfo(name = "mood")
    var mood: String? = null

    var weight: Double
        get() = (_weight * UnitManager.Units.weightRatio).roundToDecimalPlaces(1)
        set(value) {
            _weight = value / UnitManager.Units.weightRatio
        }

    /**
     * Save weight to database in kg regardless of user preference
     */
    init {
        _weight /= UnitManager.Units.weightRatio
    }

    override fun getEntryType(): EntryType = EntryType.WEIGHT

    override fun getEntryId(): Long = id

    override fun getEntryDate(): LocalDate = date

    override fun getEntryMood(): String? = mood

    override fun updateValuesFrom(entry: GymEntry) {
        if (entry is WeightEntry) {
            this.weight = entry.weight
            this.date = LocalDate(entry.date)
            this.mood = entry.mood
        }
    }

    override fun clone(): GymEntry {
        val clone = WeightEntry(this.id, LocalDate(this.date), this.weight)
        clone.mood = this.mood
        return clone
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is WeightEntry) {
            return other.id == this.id && other.weight == this.weight && other.date == this.date && this.mood == other.mood
        }
        return false
    }

    override fun toString(): String {
        return "Entry: $id, Weight: $weight, date: $date, Mood: $mood"
    }

    companion object {
        var unitString: String = "unknown"
        get() {
             return when (UnitManager.Units.weightRatio) {
                UnitManager.WeightRatio.KG -> "kg"
                UnitManager.WeightRatio.LBS -> "lbs"
                else -> "unknown"
            }
        }

        /**
         *
         */
        fun dataPointSeriesFrom(gymEntries: List<WeightEntry>): LineGraphSeries<DataPoint> {
            val reversed = gymEntries.asReversed()
            val datapoints = mutableListOf<DataPoint>()
            for (i in 0 until reversed.size) {
                datapoints.add(DataPoint(reversed[i].date.toDate(), reversed[i].weight))
            }
            return LineGraphSeries(datapoints.toTypedArray())
        }
    }

}