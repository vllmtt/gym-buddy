package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.fragments.MoodFragment
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioType
import kotlinx.android.synthetic.main.activity_add_cardio_entry.*
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class AddCardioEntry : AppCompatActivity(), MoodFragment.MoodFragmentListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    companion object {
        const val DATE_KEY = "DEFAULT DATE"
    }

    private val formatter = DateTimeFormat.forPattern("MMMM d, yyyy")

    private var selectedDate: LocalDate? = null

    private var selectedCardioType: CardioType? = null

    private var hour: Int? = null
    private var min: Int? = null
    private var sec: Int? = null

    private var distance: Double? = null

    private var mood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cardio_entry)

        supportActionBar?.title = "Add cardio"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24px)

        supportFragmentManager.beginTransaction().replace(R.id.content_layout, MoodFragment.newInstance(null), "moodFragment").commit()


        selectedDate = LocalDate.parse(intent.getStringExtra(DATE_KEY))
        date_text.setText(selectedDate?.toString(formatter))

        duration_h_label.text = CardioEntry.getHourUnitString()
        duration_m_label.text = CardioEntry.getMinuteUnitString()
        duration_s_label.text = CardioEntry.getSecondsUnitString()

        distance_main_unit_label.text = CardioEntry.getMainDistanceUnitString()

        setupTextFields()
        validateDateField()

        setupSpinner()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter<CardioType>(this, android.R.layout.simple_spinner_item, CardioType.DEFAULT_CARDIO_TYPES)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cardio_type_spinner.adapter = adapter
        cardio_type_spinner.onItemSelectedListener = this

        cardio_type_spinner.setSelection(0)
        selectedCardioType = cardio_type_spinner.getItemAtPosition(0) as CardioType?

        Log.i(this.localClassName, selectedCardioType.toString())
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View,
                                pos: Int, id: Long) {
        selectedCardioType = parent.getItemAtPosition(pos) as CardioType?

        Log.i("FGSFGF", "Updated to $selectedCardioType")
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        selectedCardioType = null
    }

    private fun setupTextFields() {
        duration_h_text.requestFocus()

        duration_h_text.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isNumberBetween(0.0, 1000.0, p0, true, "Invalid", duration_h_layout)) {
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
                if (isNumberBetween(0.0, 59.0, p0, true, "Invalid", duration_m_layout)) {
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
                if (isNumberBetween(0.0, 59.0, p0, true, "Invalid", duration_s_layout)) {
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
                distance = if (isNumberBetween(0.0, 10000.0, p0, true, "Invalid", distance_main_text_layout)) {
                    if (p0.isNullOrEmpty()) 0.0 else distance_main_text.text.toString().toDouble()
                } else {
                    null
                }
            }
        })
    }

    private fun isNumberBetween(min: Double?, max: Double?, string: CharSequence?, isEmptyValid: Boolean, errorMessage: String, errorDestination: TextInputLayout): Boolean {
        errorDestination.error = null
        val text = string?.toString() ?: ""

        if (text.isEmpty()) {
            if (!isEmptyValid) errorDestination.error = errorMessage
            return isEmptyValid
        }

        try {
            val n = text.toDouble()
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
        return distance_main_text_layout.error == null
    }


    private fun validateDateField(): Boolean = selectedDate != null

    private fun validateCardioSelection(): Boolean = selectedCardioType != null

    override fun onMoodSelection(mood: String?) {
        this.mood = mood
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        selectedDate = LocalDate().withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
        date_text.setText(selectedDate?.toString(formatter))

        validateDateField()
    }

    fun dateTextViewClicked(@Suppress("UNUSED_PARAMETER") v: View) {
        val date = selectedDate ?: LocalDate()
        val dpd = DatePickerDialog.newInstance(
                this,
                date.year,
                date.monthOfYear - 1,
                date.dayOfMonth
        )
        dpd.show(fragmentManager, "DatePickerDialog")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_entry_menu, menu)
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
            if (save()) {
                val intent = Intent()
                if (selectedDate != null) intent.putExtra(DATE_KEY, selectedDate.toString())
                setResult(Activity.RESULT_OK, intent)

                finish()
                return true
            }
            return false
        }

        return super.onOptionsItemSelected(item)
    }

    private fun save(): Boolean {
        val valid = validateDateField() && validateDurationTextFields() && validateDistanceTextFields() && validateCardioSelection()
        return if (valid) {
            val entry = CardioEntry(0, selectedDate!!)
            entry.mood = mood
            entry.setDistance(distance, true)
            entry.duration = parseDuration()
            entry.cardioType = selectedCardioType
            AsyncTask.execute {
                GymBuddyRoomDataBase.cardioEntryDao.insertAll(entry)
            }
            true
        } else {
            Snackbar.make(add_cardio_layout, "Fix errors first", Snackbar.LENGTH_SHORT).show()
            false
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

    abstract inner class CustomTextWatcher : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        abstract override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
    }
}
