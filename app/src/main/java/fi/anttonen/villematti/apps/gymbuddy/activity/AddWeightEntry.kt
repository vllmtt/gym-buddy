package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.fragments.MoodFragment
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import kotlinx.android.synthetic.main.activity_add_weight_entry.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class AddWeightEntry : AppCompatActivity(), DatePickerDialog.OnDateSetListener, MoodFragment.MoodFragmentListener {

    companion object {
        const val DATE_KEY = "DEFAULT DATE"
    }

    val DATE_FORMATTER = DateTimeFormat.forPattern("MMMM d, yyyy")

    var selectedDate: LocalDate? = null
    var weight: Double? = null
    var mood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_weight_entry)

        supportActionBar?.title = "Add weight"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24px)

        supportFragmentManager.beginTransaction().replace(R.id.content_layout, MoodFragment.newInstance(null), "moodFragment").commit()

        weight_text.requestFocus()
        weight_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                validateWeightTextField()
            }

        })

        selectedDate = LocalDate.parse(intent.getStringExtra(DATE_KEY))
        date_text.setText(selectedDate?.toString(DATE_FORMATTER))
        unit_label.text = WeightEntry.unitString
        validateDateField()
    }

    private fun validateWeightTextField(): Boolean {
        try {
            val n = weight_text.text.toString().toDouble()
            if (n < 0) {
                weight_text_layout.error = "Weight must be positive"
                weight = null
                return false
            }
            weight = n
            weight_text_layout.error = null
            weight_text.error = null
            return true
        } catch (e: NumberFormatException) {
            weight_text_layout.error = "Weight is missing"
            weight = null
            return false
        }
    }


    private fun validateDateField(): Boolean {
        if (selectedDate == null) return false
        var valid = false
        GymBuddyRoomDataBase.weightEntryDao.getAll(selectedDate!!).observe(this, android.arch.lifecycle.Observer {
            if (it == null || it.isNotEmpty()) {
                    date_text_layout.error = "Weight already exists on this date"
                    selectedDate = null
                    valid = false
            } else {
                    date_text_layout.error = null
                valid = true
            }

        })
        return valid
    }

    override fun onMoodSelection(mood: String?) {
        this.mood = mood
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        selectedDate = LocalDate().withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
        date_text.setText(selectedDate?.toString(DATE_FORMATTER))

        validateDateField()
    }

    fun dateTextViewClicked(v: View) {
        val date = selectedDate ?: LocalDate()
        val dpd = DatePickerDialog.newInstance(
                this,
                date.year,
                date.monthOfYear - 1,
                date.dayOfMonth
        )
        dpd.show(fragmentManager, "Datepickerdialog")
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
        validateDateField()
        validateWeightTextField()

        return if (selectedDate != null && weight != null) {
            val entry = WeightEntry(0, selectedDate!!, weight!!)
            entry.mood = mood
            AsyncTask.execute {
                GymBuddyRoomDataBase.weightEntryDao.insertAll(entry)
            }
            true
        } else {
            Snackbar.make(add_weight_layout, "Fix errors first", Snackbar.LENGTH_SHORT).show()
            false
        }
    }
}
