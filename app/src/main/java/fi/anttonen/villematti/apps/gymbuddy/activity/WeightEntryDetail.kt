package fi.anttonen.villematti.apps.gymbuddy.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import com.jjoe64.graphview.GridLabelRenderer
import kotlinx.android.synthetic.main.activity_weight_entry_detail.*
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.CalendarGymEntriesViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import org.joda.time.LocalDate
import android.support.v7.app.AlertDialog
import fi.anttonen.villematti.apps.gymbuddy.fragments.MoodFragment


class WeightEntryDetail : AppCompatActivity(), MoodFragment.MoodFragmentListener {

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
                supportFragmentManager.beginTransaction().replace(R.id.content_layout, MoodFragment.newInstance(clone.mood), "moodFragment").commit()
            }
        }


        supportActionBar?.title = getString(R.string.weight_entry_detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun setupWeightEditText() {
        weight_text.setText(clone.getWeightUI(1).toString())
        unit_label.text = WeightEntry.unitString
        weight_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                validateWeightTextField()
            }

        })
    }

    private fun validateWeightTextField(): Boolean {
        try {
            val n = weight_text.text.toString().toDouble()
            if (n < 0) {
                weight_text_layout.error = "Weight must be positive"
                clone.setWeight(0.0, true)
                return false
            }
            clone.setWeight(n, true)
            weight_text_layout.error = null
            setupWeightGraph()
            return true
        } catch (e: NumberFormatException) {
            weight_text_layout.error = "Weight is missing"
            clone.setWeight(0.0, true)
            return false
        }
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
                    val series = WeightEntry.dataPointSeriesFrom(data)
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

                    //weight_graph.gridLabelRenderer.numHorizontalLabels = 0
                    //weight_graph.gridLabelRenderer.numVerticalLabels = 0

                    weight_graph.viewport.setMinX(LocalDate(data.last().date).toDate().time.toDouble())
                    weight_graph.viewport.setMaxX(LocalDate(data.first().date).toDate().time.toDouble())
                    weight_graph.viewport.isXAxisBoundsManual = true

                    //weight_graph.viewport.setMinY(lowY)
                    //weight_graph.viewport.setMaxY(highY)
                    //weight_graph.viewport.isYAxisBoundsManual = false

                    weight_graph.gridLabelRenderer.setHumanRounding(true)
                }
            }
        }
    }

    override fun onMoodSelection(mood: String?) {
        clone.mood = mood
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
            if (!save()) return true

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

            AlertDialog.Builder(this)
                    .setTitle("Delete?")
                    .setMessage("This weight entry will be removed permanently.")
                    .setPositiveButton(android.R.string.yes, { _, _ -> delete().run { finish() } })
                    .setNegativeButton(android.R.string.no, null).show()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun delete() {
        AsyncTask.execute {
            calendarGymEntriesViewModel.deleteAll(entry)
        }
    }

    private fun save(): Boolean {
        if (!validateWeightTextField()) {
            Snackbar.make(weight_text, "Fix errors first", Snackbar.LENGTH_SHORT).show()
            return false
        }
        entry.updateValuesFrom(clone)
        AsyncTask.execute {
            calendarGymEntriesViewModel.updateAll(entry)
        }
        return true
    }
}

