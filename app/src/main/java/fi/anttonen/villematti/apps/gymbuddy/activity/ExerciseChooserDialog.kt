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
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.StrengthWorkoutViewModel
import fi.anttonen.villematti.apps.gymbuddy.fragments.SearchableRecyclerViewFragment
import fi.anttonen.villematti.apps.gymbuddy.misc.SearchableView
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthExercise

class ExerciseChooserDialog : AppCompatActivity(), SearchView.OnQueryTextListener, SearchableRecyclerViewFragment.SearchableRecyclerViewFragmentListener {

    companion object {
        const val SELECTED_EXERCISE_ID = "selected_exercise_id"
    }

    private var exerciseChooser = SearchableRecyclerViewFragment.newInstance("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_chooser_dialog)

        supportFragmentManager.beginTransaction().replace(R.id.exercise_chooser_content_layout, exerciseChooser, "searchableRecyclerViewFragment").commit()

        val strengthWorkoutViewModel = ViewModelProviders.of(this).get(StrengthWorkoutViewModel::class.java)
        strengthWorkoutViewModel.getAllExercises(true).observe(this, Observer { exercises ->
            exerciseChooser.updateViewItems(exercises as List<SearchableView>)
        })

        supportActionBar?.title = "Choose an exercise"
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
        exerciseChooser.updateQuery(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        exerciseChooser.updateQuery(newText)
        return false
    }

    override fun onItemSelection(selectedItem: SearchableView?) {
        val intent = Intent()
        if (selectedItem != null) intent.putExtra(SELECTED_EXERCISE_ID, (selectedItem as StrengthExercise).id)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
