/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import fi.anttonen.villematti.apps.gymbuddy.CardioWorkoutViewModel
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.StrengthWorkoutViewModel
import fi.anttonen.villematti.apps.gymbuddy.fragments.SearchableRecyclerViewFragment
import fi.anttonen.villematti.apps.gymbuddy.misc.SearchableView
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioType
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthExercise

class ExerciseEditorList : AppCompatActivity(), SearchView.OnQueryTextListener, SearchableRecyclerViewFragment.SearchableRecyclerViewFragmentListener {

    companion object {
        const val STRENGTH_EXERCISES = "STRENGTH_EXERCISES"
        const val CARDIO_EXERCISES = "CARDIO_EXERCISES"

        private const val REQUEST_ADD = 0
        private const val REQUEST_EDIT = 1

        private enum class EditorType {
            CARDIO, STRENGTH, BOTH
        }
    }

    private var editorType: EditorType? = null

    private var exerciseChooser = SearchableRecyclerViewFragment.newInstance("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_editor)

        if (savedInstanceState == null) { // TODO null check also to ExerciseChooserDialog
            supportFragmentManager.beginTransaction().add(R.id.exercise_chooser_content_layout, exerciseChooser, "searchableRecyclerViewFragment").commit()
        }

        val displayStrengthExercises = intent.getBooleanExtra(STRENGTH_EXERCISES, false)
        val displayCardioExercises = intent.getBooleanExtra(CARDIO_EXERCISES, false)

        if (!displayCardioExercises && !displayStrengthExercises) throw RuntimeException("No exercise type specified in ExerciseChooserDialog")
        if (displayCardioExercises && displayStrengthExercises) throw RuntimeException("Both cardio and strength exercise types not currently supported by ExerciseChooserDialog")
        editorType = if (displayStrengthExercises) EditorType.STRENGTH else EditorType.CARDIO


        if (displayStrengthExercises) {
            val strengthWorkoutViewModel = ViewModelProviders.of(this).get(StrengthWorkoutViewModel::class.java)
            strengthWorkoutViewModel.getAllExercises(false).observe(this, Observer { exercises ->
                exerciseChooser.updateViewItems(exercises as List<SearchableView>)
            })
        }
        if (displayCardioExercises) {
            val cardioWorkoutViewModel = ViewModelProviders.of(this).get(CardioWorkoutViewModel::class.java)
            cardioWorkoutViewModel.getAllExercises(false).observe(this, Observer { exercises ->
                exerciseChooser.updateViewItems(exercises as List<SearchableView>)
            })
        }

        supportActionBar?.title = "Strength exercises"
    }

    fun addButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        if (editorType == EditorType.STRENGTH) {
            openStrengthExerciseEditor()
        } else {
            openCardioExerciseEditor()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == android.R.id.home) {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false //TODO
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false //TODO
    }

    override fun onItemSelection(selectedItem: SearchableView?) {
        if (editorType == EditorType.STRENGTH) {
            openStrengthExerciseEditor((selectedItem as StrengthExercise).id)
        } else {
            openCardioExerciseEditor((selectedItem as CardioType).id)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ADD && resultCode == Activity.RESULT_OK) {
            // TODO update list
            return
        }
        if (requestCode == REQUEST_EDIT && resultCode == Activity.RESULT_OK) {
            // TODO update list
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openStrengthExerciseEditor(id: Long? = null) {
        val request = if (id == null) REQUEST_ADD else REQUEST_EDIT
        val intent = if (id == null) {
            Intent(this, StrengthExerciseEditor::class.java).apply {
                putExtra(StrengthExerciseEditor.ADD_MODE, true)
            }
        } else {
            Intent(this, StrengthExerciseEditor::class.java).apply {
                putExtra(StrengthExerciseEditor.EDIT_MODE, true)
                putExtra(StrengthExerciseEditor.EXERCISE_ID_KEY, id)
            }
        }
        ActivityCompat.startActivityForResult(this, intent, request, null)
    }

    private fun openCardioExerciseEditor(id: Long? = null) {
        TODO("Not implemented")
    }
}
