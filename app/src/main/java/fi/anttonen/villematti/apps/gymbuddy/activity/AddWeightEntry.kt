package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_weight_entry)

        supportActionBar?.title = "Add weight"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24px)

        weight_text.requestFocus()

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
            save()

            val intent = Intent()
            if (selectedDate != null) intent.putExtra(DATE_KEY, selectedDate.toString())
            setResult(Activity.RESULT_OK, intent)

            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        //TODO
    }
}
