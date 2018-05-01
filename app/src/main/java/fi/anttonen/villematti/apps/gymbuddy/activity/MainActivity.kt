package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.sundeepk.compactcalendarview.AnimationListener
import com.github.sundeepk.compactcalendarview.AnimatorListener
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymEntry
import kotlinx.android.synthetic.main.activity_main.*
import fi.anttonen.villematti.apps.gymbuddy.control.GymEntriesRecyclerAdapter
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.WeightEntry
import kotlinx.android.synthetic.main.activity_main.view.*
import java.text.SimpleDateFormat
import java.util.*
import android.animation.Animator
import android.animation.AnimatorListenerAdapter


class MainActivity : AppCompatActivity(), CompactCalendarView.CompactCalendarViewListener, GymEntriesRecyclerAdapter.OnItemClickListener {

    companion object {
        private const val UPDATE_REQUEST = 1
    }

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: GymEntriesRecyclerAdapter
    private var calHidden = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager = LinearLayoutManager(this)
        gymEntriesRecyclerView.layoutManager = linearLayoutManager

        adapter = GymEntriesRecyclerAdapter()
        adapter.itemClickListener = this
        gymEntriesRecyclerView.adapter = adapter

        setupCalendar()

        supportActionBar?.title = getMainTitle(null)
    }

    private fun setupCalendar() {
        calendar_view.shouldSelectFirstDayOfMonthOnScroll(false)
        calendar_view.setListener(this)
    }


    override fun onDayClick(dateClicked: Date?) {

    }

    override fun onMonthScroll(firstDayOfNewMonth: Date?) {
        supportActionBar?.title = getMainTitle(firstDayOfNewMonth)
    }


    /**
     * Item click listener for gym entry recycler view
     */
    override fun onItemClick(view: View, position: Int, entry: GymEntry) {
        if (entry is WeightEntry) {
            val weightDetailIntent = Intent(this, WeightEntryDetail::class.java).apply {
                putExtra(WeightEntryDetail.ENTRY_ID_KEY, entry.getEntryId())
            }

            //val weightTextPair = Pair.create(view.weight_text as View, "tWeightText")
            //val weightUnitTextPair = Pair.create(view.weight_unit_text as View, "tWeightUnitText")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity)//,
            //        weightTextPair, weightUnitTextPair)

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
            if (!calHidden) {
                calendar_view.animate().translationY(-calendar_view.height.toFloat()).setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        calendar_view.visibility = View.GONE
                    }
                })
                calHidden = true
            } else {
                calendar_view.animate().translationY(0f).setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        calendar_view.visibility = View.VISIBLE
                    }
                })
                calHidden = false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getMainTitle(date: Date?): String {
        val cal = Calendar.getInstance()
        if (date != null) cal.time = date
        val f = SimpleDateFormat("MMMM yyyy")
        return f.format(cal.time)
    }

}
