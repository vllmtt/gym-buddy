package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.R.id.new_cardio_entry
import fi.anttonen.villematti.apps.gymbuddy.R.id.new_weight_entry
import fi.anttonen.villematti.apps.gymbuddy.adapters.CalendarGymEntriesRecyclerAdapter
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.model.CalendarEventViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.CalendarGymEntriesViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.entity.EntryType
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import kotlinx.android.synthetic.main.activity_main.*
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.util.*


class MainActivity : AppCompatActivity(), CompactCalendarView.CompactCalendarViewListener, CalendarGymEntriesRecyclerAdapter.OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val UPDATE_REQUEST = 1
        private const val ADD_ENTRY = 1
    }

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var adapter: CalendarGymEntriesRecyclerAdapter? = null
    private lateinit var currentlySelectedDate: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        if (false) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        JodaTimeAndroid.init(this)
        setupCalendar()

        linearLayoutManager = LinearLayoutManager(this)
        gymEntriesRecyclerView.layoutManager = linearLayoutManager

        supportActionBar?.title = getMainTitle(null)

        setupSpeedDial()

        ////////////// INIT DATABASE & PREFS //////////////
        // TODO These to every activity just in case
        GymBuddyRoomDataBase.initIfNull(applicationContext)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        preferences.getBoolean(getString(R.string.flag_db_initialized), false).let { initialized ->
            if (!initialized) {
                AsyncTask.execute {
                    GymBuddyRoomDataBase.initData()
                    with (preferences.edit()) {
                        putBoolean(getString(R.string.flag_db_initialized), true)
                        apply()
                    }
                }
            }
        }

        preferences.getString(getString(R.string.pref_weight_unit_key), getString(R.string.weight_unit_kilograms_key)).apply {
            when (this) {
                getString(R.string.weight_unit_kilograms_key) -> UnitManager.Units.weightRatio = UnitManager.WeightRatio.KG
                getString(R.string.weight_unit_pounds_key) -> UnitManager.Units.weightRatio = UnitManager.WeightRatio.LBS
            }
        }
        preferences.getString(getString(R.string.pref_distance_unit_key), getString(R.string.distance_unit_kilometers_key)).apply {
            when (this) {
                getString(R.string.distance_unit_kilometers_key) -> UnitManager.Units.distanceRatio = UnitManager.DistanceRatio.KM
                getString(R.string.distance_unit_miles_key) -> UnitManager.Units.distanceRatio = UnitManager.DistanceRatio.M
            }
        }
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

        ///////////// INIT LIVE DATA OBSERVING ///////////////

        val gymEntriesViewModel = ViewModelProviders.of(this).get(CalendarGymEntriesViewModel::class.java)
        gymEntriesViewModel.setDateFilter(currentlySelectedDate)
        gymEntriesViewModel.getGymEntriesForDate().observe(this, android.arch.lifecycle.Observer { entries ->
            if (gymEntriesRecyclerView.adapter == null) {
                adapter = CalendarGymEntriesRecyclerAdapter(entries, gymEntriesViewModel)
                gymEntriesRecyclerView.adapter = adapter
                adapter?.itemClickListener = this
            } else {
                adapter?.updateGymEntries(entries)
            }
        })

        val calendarEventViewModel = ViewModelProviders.of(this).get(CalendarEventViewModel::class.java)
        calendarEventViewModel.setDateFilter(currentlySelectedDate)
        calendarEventViewModel.getGymEntriesForDateRange().observe(this, android.arch.lifecycle.Observer { entries ->
            if (entries != null) {
                calendar_view.removeAllEvents()
                entryLoop@ for (entry in entries) {
                    when (entry.getEntryType()) {
                        EntryType.WEIGHT -> calendar_view.addEvent(Event(ContextCompat.getColor(this, R.color.weight), entry.getEntryDate().toDate().time))
                        EntryType.CARDIO -> calendar_view.addEvent(Event(ContextCompat.getColor(this, R.color.cardio), entry.getEntryDate().toDate().time))
                        else -> continue@entryLoop
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        calendar_view.setCurrentDate(currentlySelectedDate.toDate())
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun setupSpeedDial() {
        speedDial.inflate(R.menu.speed_dial_menu)
        speedDial.setOnActionSelectedListener {
            when (it.id) {
                new_weight_entry -> showNewWeightEntryDialog()
                new_cardio_entry -> showNewCardioEntryDialog()
                else -> false
            }
        }
    }

    private fun showNewCardioEntryDialog(): Boolean {
        val intent = Intent(this, AddCardioEntry::class.java).apply {
            putExtra(AddCardioEntry.DATE_KEY, currentlySelectedDate.toString())
        }
        ActivityCompat.startActivityForResult(this, intent, ADD_ENTRY, null)
        return false
    }

    private fun showNewWeightEntryDialog(): Boolean {
        val addWeightIntent = Intent(this, AddWeightEntry::class.java).apply {
            putExtra(AddWeightEntry.DATE_KEY, currentlySelectedDate.toString())
        }
        ActivityCompat.startActivityForResult(this, addWeightIntent, ADD_ENTRY, null)
        return false
    }

    private fun setupCalendar() {
        calendar_view.shouldDrawIndicatorsBelowSelectedDays(true)
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
        ViewModelProviders.of(this).get(CalendarEventViewModel::class.java).setDateFilter(LocalDate(firstDayOfNewMonth))
    }


    /**
     * Item click listener for gym entry recycler view
     */
    override fun onItemClick(view: View, position: Int, entry: GymEntry?) {
        if (entry is WeightEntry) {
            val weightDetailIntent = Intent(this, WeightEntryDetail::class.java).apply {
                putExtra(WeightEntryDetail.ENTRY_ID_KEY, entry.getEntryId())
            }

            val options = ActivityOptionsCompat.makeBasic()
            ActivityCompat.startActivityForResult(this@MainActivity, weightDetailIntent, UPDATE_REQUEST, options.toBundle())
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_ENTRY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val stringExtra = data.getStringExtra(AddWeightEntry.DATE_KEY)
                if (stringExtra != null) {
                    val dateWhereAdditionTookPlace = LocalDate.parse(stringExtra)
                    if (dateWhereAdditionTookPlace != null) {
                        calendar_view.setCurrentDate(dateWhereAdditionTookPlace.toDate())

                        currentlySelectedDate = dateWhereAdditionTookPlace
                        supportActionBar?.title = getMainTitle(currentlySelectedDate)
                    }
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
        if (id == R.id.menu_item_calendar_toggle) {
            if (calendar_view.visibility == View.VISIBLE) {
                calendar_view.visibility = View.GONE
                item.icon = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_down_white_24px)
            } else {
                calendar_view.visibility = View.VISIBLE
                item.icon = ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_up_white_24px)
            }
            calendar_view.setCurrentDate(currentlySelectedDate.toDate())
            supportActionBar?.title = getMainTitle(currentlySelectedDate)
        }
        if (id == R.id.menu_item_today) {
            currentlySelectedDate = LocalDate.now()
            calendar_view.setCurrentDate(currentlySelectedDate.toDate())
            ViewModelProviders.of(this).get(CalendarGymEntriesViewModel::class.java).setDateFilter(currentlySelectedDate)
            ViewModelProviders.of(this).get(CalendarEventViewModel::class.java).setDateFilter(LocalDate(currentlySelectedDate))
            supportActionBar?.title = getMainTitle(currentlySelectedDate)
        }
        if (id == R.id.menu_item_settings) {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            ActivityCompat.startActivity(this, settingsIntent, null)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        val weightSettingKey = getString(R.string.pref_weight_unit_key)
        val distanceSettingKey = getString(R.string.pref_distance_unit_key)
        if (key == weightSettingKey || key == distanceSettingKey) {
            gymEntriesRecyclerView.adapter.notifyDataSetChanged()
        }
    }

    private fun getMainTitle(date: LocalDate?): String {
        val pattern = if (calendar_view.visibility == View.VISIBLE) "MMMM yyyy" else "MMMM d, yyyy"
        val f = DateTimeFormat.forPattern(pattern)
        return date?.toString(f) ?: LocalDate.now().toString(f)
    }

}
