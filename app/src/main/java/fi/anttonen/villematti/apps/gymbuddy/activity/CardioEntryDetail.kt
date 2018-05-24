package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.fragments.MoodFragment
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.model.CalendarGymEntriesViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioType
import kotlinx.android.synthetic.main.activity_cardio_entry_detail.*
import org.joda.time.Duration

class CardioEntryDetail : AppCompatActivity(), MoodFragment.MoodFragmentListener, AdapterView.OnItemSelectedListener {

    companion object {
        const val ENTRY_ID_KEY = "ENTRY_KEY"
        const val CHANGED_KEY = "CHANGED"
        const val DELETED_KEY = "DELETED"
    }

    private var hour: Int? = null
    private var min: Int? = null
    private var sec: Int? = null

    private var distanceMain: Int? = null
    private var distanceSecondary: Int? = null

    private var mood: String? = null

    private lateinit var entry: CardioEntry
    private lateinit var clone: CardioEntry

    private lateinit var calendarGymEntriesViewModel: CalendarGymEntriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cardio_entry_detail)

        supportActionBar?.title = "Add cardio"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24px)

        val id = intent.getLongExtra(ENTRY_ID_KEY, -1)

        calendarGymEntriesViewModel = ViewModelProviders.of(this).get(CalendarGymEntriesViewModel::class.java)
        AsyncTask.execute {
            entry = calendarGymEntriesViewModel.getCardioEntry(id)!!
            clone = entry.clone() as CardioEntry
            runOnUiThread {
                supportFragmentManager.beginTransaction().replace(R.id.cardio_detail_layout, MoodFragment.newInstance(clone.mood), "moodFragment").commit()

                date_text.text = clone.getHumanReadableDate(this)

                duration_h_label.text = CardioEntry.getHourUnitString()
                duration_m_label.text = CardioEntry.getMinuteUnitString()
                duration_s_label.text = CardioEntry.getSecondsUnitString()

                distance_main_unit_label.text = CardioEntry.getMainDistanceUnitString()
                distance_secondary_label.text = CardioEntry.getSecondaryDistanceUnitString()

                unparseDuration(clone.duration)
                unparseDistance(clone.getDistanceUI(1))
                setupTextFields()

                setupSpinner()
            }
        }

    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter<CardioType>(this, android.R.layout.simple_spinner_item, CardioType.DEFAULT_CARDIO_TYPES)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cardio_type_spinner.adapter = adapter
        cardio_type_spinner.onItemSelectedListener = this

        val position = adapter.getPosition(clone.cardioType)
        cardio_type_spinner.setSelection(position)

        Log.i("FGSFGF", "Pos: $position of ${clone.cardioType}")
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View,
                                pos: Int, id: Long) {
        clone.cardioType = parent.getItemAtPosition(pos) as CardioType?

        Log.i("FGSFGF", "Updated to ${clone.cardioType}")
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        clone.cardioType = null

        Log.i("FGSFGF", "Nothing selected to ${clone.cardioType}")
    }

    private fun setupTextFields() {
        //duration_h_text.requestFocus()

        duration_h_text.setText(hour.toString())
        duration_m_text.setText(min.toString())
        duration_s_text.setText(sec.toString())
        distance_main_text.setText(distanceMain.toString())
        distance_secondary_text.setText(distanceSecondary.toString())

        duration_h_text.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isNumberBetween(0, 1000, p0, true, "Invalid", duration_h_layout)) {
                    hour = if (p0.isNullOrEmpty()) 0 else duration_h_text.text.toString().toInt()
                    if (p0.toString().length > 1) {
                        duration_m_text.requestFocus()
                    }
                } else {
                    hour = null
                }
            }
        })
        duration_m_text.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isNumberBetween(0, 59, p0, true, "Invalid", duration_m_layout)) {
                    min = if (p0.isNullOrEmpty()) 0 else duration_m_text.text.toString().toInt()
                    if (p0.toString().length > 1) {
                        duration_s_text.requestFocus()
                    }
                } else {
                    min = null
                }
            }
        })
        duration_s_text.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isNumberBetween(0, 59, p0, true, "Invalid", duration_s_layout)) {
                    sec = if (p0.isNullOrEmpty()) 0 else duration_s_text.text.toString().toInt()
                    if (p0.toString().length > 1) {
                        distance_main_text.requestFocus()
                    }
                } else {
                    sec = null
                }
            }
        })

        distance_main_text.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val max = if (UnitManager.Units.distanceRatio == UnitManager.DistanceRatio.KM) UnitManager.DistanceRatio.METERS_IN_KM else UnitManager.DistanceRatio.FEET_IN_M
                if (isNumberBetween(0, max, p0, true, "Invalid", distance_main_text_layout)) {
                    distanceMain = if (p0.isNullOrEmpty()) 0 else distance_main_text.text.toString().toInt()
                    if (p0.toString().length > 1) {
                        distance_secondary_text.requestFocus()
                    }
                } else {
                    distanceMain = null
                }
            }
        })
        distance_secondary_text.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                distanceSecondary = if (isNumberBetween(0, 999, p0, true, "Invalid", distance_secondary_text_layout)) {
                    if (p0.isNullOrEmpty()) 0 else distance_secondary_text.text.toString().toInt()
                } else {
                    null
                }
            }
        })
    }

    private fun isNumberBetween(min: Int?, max: Int?, string: CharSequence?, isEmptyValid: Boolean, errorMessage: String, errorDestination: TextInputLayout): Boolean {
        errorDestination.error = null
        val text = string?.toString() ?: ""

        if (text.isEmpty()) {
            if (!isEmptyValid) errorDestination.error = errorMessage
            return isEmptyValid
        }

        try {
            val n = text.toInt()
            if ((min != null && n < min) || (max != null && n > max)) {
                errorDestination.error = errorMessage
                return false
            }
            return true
        } catch (e: NumberFormatException) {
            errorDestination.error = errorMessage
        }
        return false
    }

    private fun validateDurationTextFields(): Boolean {
        return duration_h_layout.error == null && duration_m_layout.error == null && duration_s_layout.error == null
    }

    private fun validateDistanceTextFields(): Boolean {
        return distance_main_text_layout.error == null && distance_secondary_text_layout.error == null
    }

    private fun validateCardioSelection(): Boolean = clone.cardioType != null

    private fun isValid() = validateDurationTextFields() && validateDistanceTextFields() && validateCardioSelection()

    override fun onMoodSelection(mood: String?) {
        this.mood = mood
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.weight_edit_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
                    .setMessage("This cardio entry will be removed permanently.")
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
        if (!isValid()) {
            Snackbar.make(cardio_detail_layout, "Fix errors first", Snackbar.LENGTH_SHORT).show()
            return false
        }

        clone.duration = parseDuration()
        clone.setDistance(parseDistance(), true)

        entry.updateValuesFrom(clone)
        AsyncTask.execute {
            calendarGymEntriesViewModel.updateAll(entry)
        }
        return true
    }

    private fun unparseDuration(duration: Duration?) {
        if (duration != null) {
            val lHours = duration.standardHours
            val lMinutes = duration.standardMinutes - lHours * 60
            val lSeconds = duration.standardSeconds - lHours * 60 * 60 - lMinutes * 60

            hour = lHours.toInt()
            min = lMinutes.toInt()
            sec = lSeconds.toInt()
        }
    }

    private fun unparseDistance(distance: Double?) {
        if (distance != null) {
            val ratio = if (UnitManager.Units.distanceRatio == UnitManager.DistanceRatio.KM) UnitManager.DistanceRatio.METERS_IN_KM else UnitManager.DistanceRatio.FEET_IN_M
            val secondaryUnits = distance % ratio
            val mainUnits = (distance - secondaryUnits) / ratio

            distanceMain = mainUnits.toInt()
            distanceSecondary = secondaryUnits.toInt()
        }
    }

    private fun parseDuration(): Duration? {
        if (hour == null && min == null && sec == null) return null
        val lHour = hour?.toLong() ?: 0L
        val lMin = min?.toLong() ?: 0L
        val lSec = sec?.toLong() ?: 0L
        if (lHour == 0L && lMin == 0L && lSec == 0L) return null
        return Duration.standardSeconds(lHour * 60 * 60 + lMin * 60 + lSec)
    }

    private fun parseDistance(): Double? {
        if (distanceMain == null && distanceSecondary == null) return null
        val multiplier = if (UnitManager.Units.distanceRatio == UnitManager.DistanceRatio.KM) UnitManager.DistanceRatio.METERS_IN_KM else UnitManager.DistanceRatio.FEET_IN_M
        val dMain = distanceMain?.toDouble() ?: 0.0
        val dSecondary = distanceSecondary?.toDouble() ?: 0.0
        return dMain * multiplier + dSecondary
    }

    abstract inner class CustomTextWatcher : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        abstract override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
    }
}
