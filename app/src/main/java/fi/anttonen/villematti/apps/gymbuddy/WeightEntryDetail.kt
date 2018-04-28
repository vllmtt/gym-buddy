package fi.anttonen.villematti.apps.gymbuddy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.util.Log
import com.jjoe64.graphview.GridLabelRenderer
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.DataSource
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.EntryType
import kotlinx.android.synthetic.main.activity_weight_entry_detail.*
import java.text.DateFormat
import java.util.*


class WeightEntryDetail : AppCompatActivity() {

    companion object {
        const val ENTRY_ID_KEY = "ENTRY_KEY"
    }

    private lateinit var entry: WeightEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_entry_detail)

        val id = intent.getStringExtra(ENTRY_ID_KEY)
        entry = DataSource.DATA_SOURCE.getGymEntry(id) as WeightEntry


        val dateFormat = android.text.format.DateFormat.getDateFormat(this)
        val dateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(entry.getEntryDate());

        date_Text.text = dateString
        weight_text.text = entry.weight.toString()
        weight_unit_text.text = entry.getUnitString()

        val data = DataSource.DATA_SOURCE.getGymEntriesBefore(entry, 5, EntryType.WEIGHT) as List<WeightEntry>
        val series = entry.dataPointSeriesFrom(data)
        series.color =  ContextCompat.getColor(this, R.color.colorAccent)
        series.isDrawDataPoints = false
        series.thickness = 4
        weight_graph.addSeries(series)

        weight_graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        weight_graph.gridLabelRenderer.isVerticalLabelsVisible = true
        weight_graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
        weight_graph.gridLabelRenderer.horizontalAxisTitle = " "

        supportActionBar?.title = getString(R.string.weight_entry_detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
