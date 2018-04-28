package fi.anttonen.villematti.apps.gymbuddy

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import com.jjoe64.graphview.GridLabelRenderer
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.DataSource
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.EntryType
import kotlinx.android.synthetic.main.activity_weight_entry_detail.*
import java.text.DateFormat
import java.util.*


class WeightEntryDetail : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val ENTRY_ID_KEY = "ENTRY_KEY"
    }

    private lateinit var entry: WeightEntry
    private var newDate: Date? = null
    private var newWeight: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_entry_detail)


        val id = intent.getStringExtra(ENTRY_ID_KEY)
        entry = DataSource.DATA_SOURCE.getGymEntry(id) as WeightEntry


        weight_text.addTextChangedListener(object: TextWatcher {
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
                    newWeight = n
                    weight_text_layout.error = null
                } catch (e: NumberFormatException) {
                    weight_text_layout.error = "Weight is missing"
                }
            }

        })


        val cal = Calendar.getInstance()
        cal.time = entry.getEntryDate()
        val dateString = getDateString(cal.time)
        date_text.setText(dateString)

        weight_text.setText(entry.weight.toString())
        unit_label.text = entry.getUnitString()

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        weight_text.clearFocus()

        setupWeightGraph()

        supportActionBar?.title = getString(R.string.weight_entry_detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, monthOfYear, dayOfMonth)
        val dateString = getDateString(cal.time)
        newDate = cal.time
        date_text.setText(dateString)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.weight_edit_menu, menu)
        return true
    }



    private fun getDateString(date: Date): String? {
        val dateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
        return dateString
    }

    fun dateTextViewClicked(v: View) {
        Log.i(this.localClassName, "Clicked")
        val date = Calendar.getInstance()
        date.time = entry.getEntryDate()
        val dpd = DatePickerDialog.newInstance(
                this@WeightEntryDetail,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show(fragmentManager, "Datepickerdialog")
    }


    /**
     * Initializes the weight history graph
     */
    private fun setupWeightGraph() {
        val data = DataSource.DATA_SOURCE.getGymEntriesBefore(entry, 5, EntryType.WEIGHT) as List<WeightEntry>
        val series = entry.dataPointSeriesFrom(data)
        series.color = ContextCompat.getColor(this, R.color.colorAccent)
        series.isDrawDataPoints = false
        series.thickness = 4
        weight_graph.addSeries(series)

        weight_graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        weight_graph.gridLabelRenderer.isVerticalLabelsVisible = true
        weight_graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
        weight_graph.gridLabelRenderer.horizontalAxisTitle = " "
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        if (id == R.id.menu_item_done) {
            save()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        if (newDate != null) {
            entry.date = newDate!!
        }
        if (newWeight != null) {
            entry.weight = newWeight!!

        }
        //TODO: DATA_SOURCE.saveEntry(entry)
    }
}
