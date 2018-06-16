/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jmedeisis.draglinearlayout.DragLinearLayout
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.WorkoutEditViewModel
import fi.anttonen.villematti.apps.gymbuddy.fragments.ExerciseFragment
import fi.anttonen.villematti.apps.gymbuddy.fragments.MoodFragment
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthWorkoutEntry
import kotlinx.android.synthetic.main.activity_add_strength_workout_entry.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class AddStrengthWorkoutEntry : AppCompatActivity(), DatePickerDialog.OnDateSetListener, MoodFragment.MoodFragmentListener, DragLinearLayout.OnViewSwapListener, ExerciseFragment.ExerciseFragmentListener {

    companion object {
        const val DATE_KEY = "DEFAULT DATE"
        const val EXERCISE_CHOOSE_REQUEST = 0
    }

    private val formatter = DateTimeFormat.forPattern("MMMM d, yyyy")
    private var selectedDate: LocalDate? = null
    private var mood: String? = null

    private var workout = StrengthWorkoutEntry(0, LocalDate.now())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_strength_workout_entry)

        supportActionBar?.title = "Add workout"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24px)

        ViewModelProviders.of(this).get(WorkoutEditViewModel::class.java).workout = workout

        for (exerciseId in workout.getExerciseMap().keys) {
            addExerciseViewFragment(exerciseId)
        }

        supportFragmentManager.beginTransaction().replace(R.id.content_layout, MoodFragment.newInstance(null), "moodFragment").commit()

        selectedDate = LocalDate.parse(intent.getStringExtra(DATE_KEY))
        date_text.setText(selectedDate?.toString(formatter))

        exercises_layout.setOnViewSwapListener(this)
        setAllExercisesDraggable()
    }

    override fun onMoodSelection(mood: String?) {
        this.mood = mood
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        selectedDate = LocalDate().withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
        date_text.setText(selectedDate?.toString(formatter))
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

    override fun onSwap(firstView: View?, firstPosition: Int, secondView: View?, secondPosition: Int) {
        // TODO update sequence or multiple sequences of sets depending if exercise or set was dragged
    }

    fun addExerciseButtonClicked(@Suppress("UNUSED_PARAMETER") v: View) {
        showExerciseChooser()
    }

    private fun showExerciseChooser() {
        val intent = Intent(this, ExerciseChooserDialog::class.java)
        val options = ActivityOptionsCompat.makeBasic()
        ActivityCompat.startActivityForResult(this, intent, EXERCISE_CHOOSE_REQUEST, options.toBundle())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EXERCISE_CHOOSE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val exerciseId = data.getLongExtra(ExerciseChooserDialog.SELECTED_EXERCISE_ID, -1)
                addExerciseViewFragment(exerciseId)
            }
        }
    }

    private fun addExerciseViewFragment(exerciseId: Long) {
        supportFragmentManager.beginTransaction().add(R.id.exercises_layout, ExerciseFragment.newInstance(exerciseId), "exercise $exerciseId").commitAllowingStateLoss()
    }

    override fun exerciseViewAdded(view: View) {
        exercises_layout.setViewDraggable(view, view.findViewById(R.id.drag_handle))
    }

    private fun setAllExercisesDraggable() {
        for (i in 0 until exercises_layout.childCount) {
            val child = exercises_layout.getChildAt(i)
            exercises_layout.setViewDraggable(child, child.findViewById(R.id.drag_handle))
        }
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
        return if (false) {
            //TODO set correct mood and date
            AsyncTask.execute {
                //GymBuddyRoomDataBase.weightEntryDao.insertAll(entry)
            }
            true
        } else {
            Snackbar.make(add_strength_workout_layout, "Fix errors first", Snackbar.LENGTH_SHORT).show()
            false
        }
    }
}