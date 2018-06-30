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
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jmedeisis.draglinearlayout.DragLinearLayout
import fi.anttonen.villematti.apps.gymbuddy.EditWorkoutViewModel
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.fragments.ExerciseFragment
import fi.anttonen.villematti.apps.gymbuddy.fragments.MoodFragment
import fi.anttonen.villematti.apps.gymbuddy.misc.WorkoutCoordinator
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthWorkoutEntry
import kotlinx.android.synthetic.main.activity_strength_workout_entry_detail.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class StrengthWorkoutEntryDetail : AppCompatActivity() , MoodFragment.MoodFragmentListener, DragLinearLayout.OnViewSwapListener, ExerciseFragment.ExerciseFragmentListener {

    companion object {
        const val ENTRY_ID_KEY = "ENTRY_KEY"
        const val CHANGED_KEY = "CHANGED"
        const val DELETED_KEY = "DELETED"
        const val EXERCISE_CHOOSE_REQUEST = 0
    }

    private var mood: String? = null

    private var editMode = false

    private var exerciseFragments = mutableListOf<ExerciseFragment>()
    private lateinit var workoutCoordinator: WorkoutCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strength_workout_entry_detail)

        supportActionBar?.title = "Edit workout"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24px)

        val id = intent.getLongExtra(ENTRY_ID_KEY, -1)
        AsyncTask.execute {
            val workout = GymBuddyRoomDataBase.strengthWorkoutEntryDao.get(id)
            runOnUiThread {
                initializeUI(savedInstanceState, workout)
            }
        }

    }

    private fun initializeUI(savedInstanceState: Bundle?, workout: StrengthWorkoutEntry) {
        val viewModel = ViewModelProviders.of(this).get(EditWorkoutViewModel::class.java)
        if (viewModel.workoutCoordinator == null) {
            workoutCoordinator = WorkoutCoordinator(workout)
            viewModel.workoutCoordinator = workoutCoordinator
        } else {
            workoutCoordinator = viewModel.workoutCoordinator!!
        }

        date_text.text = workoutCoordinator.workout.getHumanReadableDate(this)

        for (f in supportFragmentManager.fragments) {
            if (f is ExerciseFragment) exerciseFragments.add(f)
        }

        if (savedInstanceState == null) {
            for (entry in workoutCoordinator.sequenceIdMap) {
                val exerciseSequence = entry.key
                val exerciseId = entry.value
                addExerciseViewFragment(exerciseSequence, exerciseId)
            }

            supportFragmentManager.beginTransaction().add(R.id.content_layout, MoodFragment.newInstance(null), "moodFragment").commit()
        }

        toggle_delete_mode_button.visibility = if (exerciseFragments.isEmpty()) View.GONE else View.VISIBLE

        exercises_layout.setOnViewSwapListener(this)
        setAllExercisesDraggable()
    }

    override fun onMoodSelection(mood: String?) {
        this.mood = mood
    }

    override fun onSwap(firstView: View?, firstPosition: Int, secondView: View?, secondPosition: Int) {
        workoutCoordinator.swap(firstView?.tag as Int, secondView?.tag as Int)
    }

    fun toggleDeleteModeButtonClicked(@Suppress("UNUSED_PARAMETER") v: View) {
        editMode = !editMode
        for (exerciseFragment in exerciseFragments) {
            exerciseFragment.setEditMode(editMode)
        }
    }

    fun addExerciseButtonClicked(@Suppress("UNUSED_PARAMETER") v: View) {
        showExerciseChooser()
    }

    private fun showExerciseChooser() {
        val intent = Intent(this, ExerciseChooserDialog::class.java).apply {
            this.putExtra(ExerciseChooserDialog.STRENGTH_EXERCISES, true)
        }
        val options = ActivityOptionsCompat.makeBasic()
        ActivityCompat.startActivityForResult(this, intent, EXERCISE_CHOOSE_REQUEST, options.toBundle())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EXERCISE_CHOOSE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val exerciseId = data.getLongExtra(ExerciseChooserDialog.SELECTED_EXERCISE_ID, -1)
                val exerciseSequence = workoutCoordinator.addExercise(exerciseId)
                addExerciseViewFragment(exerciseSequence, exerciseId)
            }
        }
    }

    private fun addExerciseViewFragment(exerciseSequence: Int, exerciseId: Long) {
        val exerciseFragment = ExerciseFragment.newInstance(exerciseSequence, exerciseId, editMode)
        exerciseFragments.add(exerciseFragment)
        supportFragmentManager.beginTransaction().add(R.id.exercises_layout, exerciseFragment, "seq $exerciseSequence").commitAllowingStateLoss()
    }

    override fun exerciseViewAdded(view: View, exerciseSequence: Int) {
        view.tag = exerciseSequence
        exercises_layout.setViewDraggable(view, view.findViewById(R.id.drag_handle))
        toggle_delete_mode_button.visibility = if (exerciseFragments.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun deleteExercise(sender: ExerciseFragment) {
        workoutCoordinator.removeExercise(sender.exerciseSequence, sender.exerciseId)
        exerciseFragments.remove(sender)
        supportFragmentManager.beginTransaction().remove(sender).commitAllowingStateLoss()
    }

    private fun setAllExercisesDraggable() {
        for (i in 0 until exercises_layout.childCount) {
            val child = exercises_layout.getChildAt(i)
            exercises_layout.setViewDraggable(child, child.findViewById(R.id.drag_handle))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.weight_edit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        /*
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
                setResult(Activity.RESULT_OK, intent)

                finish()
                return true
            }
            return false
        }

        return super.onOptionsItemSelected(item)
        */
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
            intent.putExtra(ENTRY_ID_KEY, workoutCoordinator.workout.id)
            intent.putExtra(CHANGED_KEY, true)
            setResult(Activity.RESULT_OK, intent)

            finish()
            return true
        }

        if (id == R.id.menu_item_delete) {
            val intent = Intent()
            intent.putExtra(ENTRY_ID_KEY, workoutCoordinator.workout.id)
            intent.putExtra(DELETED_KEY, true)
            setResult(Activity.RESULT_OK, intent)

            AlertDialog.Builder(this)
                    .setTitle("Delete?")
                    .setMessage("This workout will be removed permanently.")
                    .setPositiveButton(android.R.string.yes) { _, _ -> delete().run { finish() } }
                    .setNegativeButton(android.R.string.no, null).show()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun delete() {
        AsyncTask.execute {
            workoutCoordinator.delete()
            GymBuddyRoomDataBase.strengthWorkoutEntryDao.deleteAll(workoutCoordinator.workout)
        }
    }

    private fun save(): Boolean {
        val workout = workoutCoordinator.workout
        val valid = true // TODO Validate all exercise fragments, do also to AddStrengthWorkoutEntry
        return if (valid) {
            workout.mood = mood
            AsyncTask.execute {
                workoutCoordinator.save()
                GymBuddyRoomDataBase.strengthWorkoutEntryDao.updateAll(workout)
            }
            true
        } else {
            Snackbar.make(edit_strength_workout_layout, "Fix errors first", Snackbar.LENGTH_SHORT).show()
            false
        }
    }
}