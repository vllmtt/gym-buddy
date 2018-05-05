package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
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
        get() = _weight.roundToDecimalPlaces(1)
        set(value) {
            _weight = value
        }

    override fun getEntryType(): EntryType = EntryType.WEIGHT

    override fun getEntryId(): Long {
        return id
    }

    override fun getEntryDate(): LocalDate {
        return date
    }

    override fun getEntryMood(): String? {
        return mood
    }

    override fun updateValuesFrom(entry: GymEntry) {
        if (entry is WeightEntry) {
            this.weight = entry.weight
            this.date = LocalDate(entry.date)
        }
    }

    override fun clone(): WeightEntry = WeightEntry(this.id, LocalDate(this.date), this.weight)

    override fun equals(other: Any?): Boolean {
        if (other != null && other is WeightEntry) {
            return other.id == this.id && other.weight == this.weight && other.date == this.date
        }
        return false
    }

    override fun toString(): String {
        return "Entry: $id, Weight: $weight, date: $date, Mood: $mood"
    }

    fun getUnitString(): String = "kg"

    /**
     *
     */
    fun dataPointSeriesFrom(gymEntries: List<WeightEntry>): LineGraphSeries<DataPoint> {
        //TODO: use date as x axis
        val reversed = gymEntries.asReversed()
        val datapoints = mutableListOf<DataPoint>()
        for (i in 0 until reversed.size) {
            datapoints.add(DataPoint(reversed[i].date.toDate(), reversed[i].weight))
        }
        return LineGraphSeries(datapoints.toTypedArray())
    }

    private fun Double.roundToDecimalPlaces(decimals: Int) =
            BigDecimal(this).setScale(decimals, BigDecimal.ROUND_HALF_UP).toDouble()
}