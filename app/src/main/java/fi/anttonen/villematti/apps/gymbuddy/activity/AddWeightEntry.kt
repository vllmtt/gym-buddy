package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymBuddyRoomDataBase
import kotlinx.android.synthetic.main.activity_add_weight_entry.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.util.*

class AddWeightEntry : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val DATE_KEY = "DEFAULT DATE"
    }

    val DATE_FORMATTER = DateTimeFormat.forPattern("MMMM d, yyyy")

    var selectedDate: LocalDate? = null
    var weight: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_weight_entry)

        supportActionBar?.title = "Add weight"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24px)

        weight_text.requestFocus()
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
                        weight = null
                    }
                    weight = n
                    weight_text_layout.error = null

                } catch (e: NumberFormatException) {
                    weight_text_layout.error = "Weight is missing"
                    weight = null
                }
            }

        })

        selectedDate = LocalDate.parse(intent.getStringExtra(DATE_KEY))
        date_text.setText(selectedDate?.toString(DATE_FORMATTER))
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        selectedDate = LocalDate().withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
        date_text.setText(selectedDate?.toString(DATE_FORMATTER))
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
        if (selectedDate != null && weight != null) {
            val entry = WeightEntry(0, selectedDate!!, weight!!)
            AsyncTask.execute {
                GymBuddyRoomDataBase.weightEntryDao.insertAll(entry)
            }
            return true
        } else {
            //TODO popup why can't save
            return false
        }
    }
}
