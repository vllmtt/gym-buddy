package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.misc.roundToDecimalPlaces
import org.joda.time.LocalDate

/**
 * Created by vma on 25/04/2018.
 */
@Entity(tableName = "weight_entry")
class WeightEntry(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
                  @ColumnInfo(name = "date") var date: LocalDate) : GymEntry {

    @ColumnInfo(name = "mood")
    var mood: String? = null

    private var weight = 0.0

    fun getWeightUI(decimals: Int): Double {
        return (weight * UnitManager.Units.weightRatio).roundToDecimalPlaces(decimals)
    }

    fun getWeight(): Double {
        return weight
    }

    fun setWeight(value: Double) {
        setWeight(value, false)
    }

    fun setWeight(value: Double, adjustToDb: Boolean) {
        weight = if (adjustToDb) {
            value / UnitManager.Units.weightRatio
        } else {
            value
        }
    }

    override fun getEntryType(): EntryType = EntryType.WEIGHT

    override fun getEntryId(): Long = id

    override fun getEntryDate(): LocalDate = date

    override fun getEntryMood(): String? = mood

    override fun updateValuesFrom(entry: GymEntry) {
        if (entry is WeightEntry) {
            this.setWeight(entry.getWeight(), false)
            this.date = LocalDate(entry.date)
            this.mood = entry.mood
        }
    }

    override fun clone(): GymEntry {
        val clone = WeightEntry(this.id, LocalDate(this.date))
        clone.setWeight(this.getWeight(), false)
        clone.mood = this.mood
        return clone
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is WeightEntry) {
            return other.id == this.id && other.getWeight() == this.getWeight() && other.date == this.date && this.mood == other.mood
        }
        return false
    }

    override fun toString(): String {
        return "Entry: $id, Weight (raw): ${getWeight()}, date: $date, Mood: $mood"
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + (mood?.hashCode() ?: 0)
        result = 31 * result + weight.hashCode()
        return result
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
                datapoints.add(DataPoint(reversed[i].date.toDate(), reversed[i].getWeightUI(1)))
            }
            return LineGraphSeries(datapoints.toTypedArray())
        }
    }

}