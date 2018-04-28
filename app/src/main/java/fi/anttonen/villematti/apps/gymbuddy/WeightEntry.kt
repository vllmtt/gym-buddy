package fi.anttonen.villematti.apps.gymbuddy

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.EntryType
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymEntry
import java.math.BigDecimal
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by vma on 25/04/2018.
 */
class WeightEntry(val id: String, var date: Date, private var _weight: Double) : GymEntry {



    var weight: Double
        get() = _weight.roundToDecimalPlaces(1)
        set(value) {
            _weight = value
        }

    override fun getEntryType(): EntryType = EntryType.WEIGHT

    override fun getEntryId(): String {
        return id
    }

    override fun getEntryDate(): Date {
        return date
    }

    fun getUnitString(): String = "kg"

    fun dataPointSeriesFrom(gymEntries: List<WeightEntry>): LineGraphSeries<DataPoint> {
        val reversed = gymEntries.asReversed()
        val datapoints = mutableListOf<DataPoint>()
        for (i in 0 until reversed.size) {
            datapoints.add(DataPoint((i).toDouble(), reversed[i].weight))
        }
        return LineGraphSeries(datapoints.toTypedArray())
    }

    private fun Double.roundToDecimalPlaces(decimals: Int) =
            BigDecimal(this).setScale(decimals, BigDecimal.ROUND_HALF_UP).toDouble()
}