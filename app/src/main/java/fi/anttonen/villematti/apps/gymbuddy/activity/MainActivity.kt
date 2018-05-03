package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymEntry
import kotlinx.android.synthetic.main.activity_main.*
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.control.CalendarGymEntriesRecyclerAdapter
import fi.anttonen.villematti.apps.gymbuddy.model.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.DataSource
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), CompactCalendarView.CompactCalendarViewListener, CalendarGymEntriesRecyclerAdapter.OnItemClickListener {

    companion object {
        private const val UPDATE_REQUEST = 1
    }

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CalendarGymEntriesRecyclerAdapter
    private var currentlySelectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ///////////// INIT DATABASE //////////////
        DataSource.initDatabse(applicationContext)

        setContentView(R.layout.activity_main)

        JodaTimeAndroid.init(this);

        linearLayoutManager = LinearLayoutManager(this)
        gymEntriesRecyclerView.layoutManager = linearLayoutManager

        adapter = CalendarGymEntriesRecyclerAdapter()
        adapter.itemClickListener = this
        gymEntriesRecyclerView.adapter = adapter

        setupCalendar()

        supportActionBar?.title = getMainTitle(null)
    }

    private fun setupCalendar() {
        calendar_view.shouldSelectFirstDayOfMonthOnScroll(false)
        calendar_view.setListener(this)
        currentlySelectedDate = LocalDate()
        calendar_view.setCurrentDate(currentlySelectedDate?.toDate())
        adapter.updateGymEntries(currentlySelectedDate)
    }


    override fun onDayClick(dateClicked: Date?) {
        currentlySelectedDate = LocalDate(dateClicked)
        supportActionBar?.title = getMainTitle(currentlySelectedDate)
        adapter.updateGymEntries(currentlySelectedDate)
    }

    override fun onMonthScroll(firstDayOfNewMonth: Date?) {
        supportActionBar?.title = getMainTitle(LocalDate.fromDateFields(firstDayOfNewMonth))
    }


    /**
     * Item click listener for gym entry recycler view
     */
    override fun onItemClick(view: View, position: Int, entry: GymEntry) {
        if (entry is WeightEntry) {
            val weightDetailIntent = Intent(this, WeightEntryDetail::class.java).apply {
                putExtra(WeightEntryDetail.ENTRY_ID_KEY, entry.getEntryId())
            }

            val options = ActivityOptionsCompat.makeBasic()
            ActivityCompat.startActivityForResult(this@MainActivity, weightDetailIntent, UPDATE_REQUEST, options.toBundle())
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPDATE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val changed = data.getBooleanExtra(WeightEntryDetail.CHANGED_KEY, false)
                val deleted = data.getBooleanExtra(WeightEntryDetail.DELETED_KEY, false)
                if (changed || deleted) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.menu_item_toggle_calendar) {
            calendar_view.visibility = if (calendar_view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            supportActionBar?.title = getMainTitle(currentlySelectedDate)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getMainTitle(date: LocalDate?): String {
        val pattern = if (calendar_view.visibility == View.VISIBLE) "MMMM yyyy" else "MMMM d, yyyy"
        val f = DateTimeFormat.forPattern(pattern)
        return date?.toString(f) ?: LocalDate.now().toString(f)
    }

}
