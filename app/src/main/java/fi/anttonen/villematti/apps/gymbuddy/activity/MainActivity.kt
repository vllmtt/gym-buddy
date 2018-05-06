package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.R.id.calendar_view
import fi.anttonen.villematti.apps.gymbuddy.adapters.CalendarGymEntriesRecyclerAdapter
import fi.anttonen.villematti.apps.gymbuddy.model.CalendarGymEntriesViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymBuddyRoomDataBase
import kotlinx.android.synthetic.main.activity_main.*
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.util.*


class MainActivity : AppCompatActivity(), CompactCalendarView.CompactCalendarViewListener, CalendarGymEntriesRecyclerAdapter.OnItemClickListener {

    companion object {
        private const val UPDATE_REQUEST = 1
    }

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var adapter: CalendarGymEntriesRecyclerAdapter? = null
    private lateinit var currentlySelectedDate: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        JodaTimeAndroid.init(this)
        setupCalendar()

        linearLayoutManager = LinearLayoutManager(this)
        gymEntriesRecyclerView.layoutManager = linearLayoutManager

        supportActionBar?.title = getMainTitle(null)

        ////////////// INIT DATABASE //////////////
        GymBuddyRoomDataBase.initIfNull(applicationContext)


        if (false) {
            AsyncTask.execute {
                GymBuddyRoomDataBase.initTestData()
            }
        }


        val viewModel = ViewModelProviders.of(this).get(CalendarGymEntriesViewModel::class.java)
        viewModel.setDateFilter(currentlySelectedDate)

        viewModel.getGymEntriesForDate().observe(this, android.arch.lifecycle.Observer { entries ->
            if (gymEntriesRecyclerView.adapter == null) {
                adapter = CalendarGymEntriesRecyclerAdapter(entries)
                gymEntriesRecyclerView.adapter = adapter
                adapter?.itemClickListener = this
            } else {
                adapter?.updateGymEntries(entries)
            }
        })

        viewModel.getWeightEntryHistoryForDate().observe(this, android.arch.lifecycle.Observer { history ->
            if (gymEntriesRecyclerView.adapter != null) {
                Log.i(this.localClassName, "Observing ${history?.size} historical entries")
            }
        })


    }

    private fun setupCalendar() {
        calendar_view.shouldSelectFirstDayOfMonthOnScroll(false)
        calendar_view.setListener(this)
        currentlySelectedDate = LocalDate()
        calendar_view.setCurrentDate(currentlySelectedDate.toDate())
    }


    override fun onDayClick(dateClicked: Date?) {
        currentlySelectedDate = LocalDate(dateClicked)
        supportActionBar?.title = getMainTitle(currentlySelectedDate)
        ViewModelProviders.of(this).get(CalendarGymEntriesViewModel::class.java).setDateFilter(currentlySelectedDate)
    }

    override fun onMonthScroll(firstDayOfNewMonth: Date?) {
        supportActionBar?.title = getMainTitle(LocalDate.fromDateFields(firstDayOfNewMonth))
    }


    /**
     * Item click listener for gym entry recycler view
     */
    override fun onItemClick(view: View, position: Int, entry: GymEntry?) {
        if (entry is WeightEntry) {
            /*
            val weightDetailIntent = Intent(this, WeightEntryDetail::class.java).apply {
                putExtra(WeightEntryDetail.ENTRY_ID_KEY, entry.getEntryId())
            }

            val options = ActivityOptionsCompat.makeBasic()
            ActivityCompat.startActivityForResult(this@MainActivity, weightDetailIntent, UPDATE_REQUEST, options.toBundle())
            */
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPDATE_REQUEST && resultCode == Activity.RESULT_OK) {
            /*
            if (data != null) {
                val changed = data.getBooleanExtra(WeightEntryDetail.CHANGED_KEY, false)
                val deleted = data.getBooleanExtra(WeightEntryDetail.DELETED_KEY, false)
                if (changed || deleted) {
                    adapter?.notifyDataSetChanged()
                }
            }
            */
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
