package fi.anttonen.villematti.apps.gymbuddy.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import com.jjoe64.graphview.GridLabelRenderer
import fi.anttonen.villematti.apps.gymbuddy.model.entity.EntryType
import kotlinx.android.synthetic.main.activity_weight_entry_detail.*
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.AsyncTask
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.CalendarGymEntriesViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymBuddyRoomDataBase
import org.joda.time.LocalDate
import javax.sql.DataSource


class WeightEntryDetail : AppCompatActivity() {

    companion object {
        const val ENTRY_ID_KEY = "ENTRY_KEY"
        const val CHANGED_KEY = "CHANGED"
        const val DELETED_KEY = "DELETED"
    }

    private lateinit var entry: WeightEntry
    private lateinit var clone: WeightEntry

    private lateinit var calendarGymEntriesViewModel: CalendarGymEntriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_entry_detail)

        calendarGymEntriesViewModel = ViewModelProviders.of(this).get(CalendarGymEntriesViewModel::class.java)
        val id = intent.getLongExtra(ENTRY_ID_KEY, -1)
        AsyncTask.execute {
            entry = calendarGymEntriesViewModel.getWeightEntry(id)!!
            runOnUiThread {
                clone = entry.clone() as WeightEntry

                date_text.text = clone.getHumanReadableDate(this)
                setupWeightEditText()
                setupWeightGraph()
            }
        }

        supportActionBar?.title = getString(R.string.weight_entry_detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun setupWeightEditText() {
        weight_text.setText(clone.weight.toString())
        unit_label.text = clone.getUnitString()
        weight_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    val n = weight_text.text.toString().toDouble()
                    if (n < 0) {
                        weight_text_layout.error = "Weight must be positive"
                    }
                    clone.weight = n
                    weight_text_layout.error = null

                    setupWeightGraph()
                } catch (e: NumberFormatException) {
                    weight_text_layout.error = "Weight is missing"
                }
            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.weight_edit_menu, menu)
        return true
    }


    /**
     * Initializes the weight history graph
     */
    private fun setupWeightGraph() {
        AsyncTask.execute{
            val data = mutableListOf(*calendarGymEntriesViewModel.getWeightEntryHistoryForDate(entry.date).toTypedArray())

            data.removeAt(0)
            data.add(0, clone)

            runOnUiThread {
                if (data.size > 1) {
                    val series = clone.dataPointSeriesFrom(data)
                    series.color = ContextCompat.getColor(this, R.color.colorAccent)
                    series.isDrawDataPoints = true
                    series.dataPointsRadius = 6.toFloat()
                    series.thickness = 4
                    weight_graph.removeAllSeries()
                    weight_graph.addSeries(series)

                    weight_graph.gridLabelRenderer.isHorizontalLabelsVisible = false
                    weight_graph.gridLabelRenderer.isVerticalLabelsVisible = true
                    weight_graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
                    weight_graph.gridLabelRenderer.horizontalAxisTitle = " "

                    weight_graph.viewport.setMinX(LocalDate(data.last().date).toDate().time.toDouble())
                    weight_graph.viewport.setMaxX(LocalDate(data.first().date).toDate().time.toDouble())
                    weight_graph.viewport.isXAxisBoundsManual = true

                    //weight_graph.viewport.setMinY(series.lowestValueY)
                    //weight_graph.viewport.setMaxY(series.highestValueY)
                    //weight_graph.viewport.isYAxisBoundsManual = false

                    weight_graph.gridLabelRenderer.setHumanRounding(true)
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == android.R.id.home) {

            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)

            finish()
            return true
        }
        if (id == R.id.menu_item_done) {
            save()

            val intent = Intent()
            intent.putExtra(ENTRY_ID_KEY, clone.id)
            intent.putExtra(CHANGED_KEY, true)
            setResult(Activity.RESULT_OK, intent)

            finish()
            return true
        }

        if (id == R.id.menu_item_delete) {
            val intent = Intent()
            intent.putExtra(ENTRY_ID_KEY, clone.id)
            intent.putExtra(DELETED_KEY, true)
            setResult(Activity.RESULT_OK, intent)

            delete()

            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun delete() {

    }

    private fun save() {
        entry.updateValuesFrom(clone)
        AsyncTask.execute {
            calendarGymEntriesViewModel.updateAll(entry)
        }
    }
}

