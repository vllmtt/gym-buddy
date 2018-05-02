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
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.DataSource
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.EntryType
import kotlinx.android.synthetic.main.activity_weight_entry_detail.*
import android.app.Activity
import android.content.Intent
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.WeightEntry


class WeightEntryDetail : AppCompatActivity() {

    companion object {
        const val ENTRY_ID_KEY = "ENTRY_KEY"
        const val CHANGED_KEY = "CHANGED"
        const val DELETED_KEY = "DELETED"
    }

    private lateinit var entry: WeightEntry
    private lateinit var clone: WeightEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_entry_detail)


        val id = intent.getStringExtra(ENTRY_ID_KEY)
        entry = DataSource.DATA_SOURCE.getGymEntry(id) as WeightEntry
        clone = entry.clone()

        date_text.text = clone.getHumanReadableDate(this)
        setupWeightEditText()
        setupWeightGraph()

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
        val data = DataSource.DATA_SOURCE.getGymEntriesBefore(clone, 5, EntryType.WEIGHT) as MutableList<WeightEntry>
        Log.i(this.localClassName, "Data before editing $data")
        data.removeAt(0)
        data.add(0, clone)
        Log.i(this.localClassName, "Data after editing $data")
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
        DataSource.DATA_SOURCE.delete(entry)
    }

    private fun save() {
        if (clone != entry) {
            Log.i(this.localClassName, "Saving")
            entry.updateValuesFrom(clone)
            DataSource.DATA_SOURCE.update(entry)
        }
    }
}
