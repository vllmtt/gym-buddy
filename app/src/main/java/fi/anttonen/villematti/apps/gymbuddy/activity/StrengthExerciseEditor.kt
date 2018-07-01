/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthExercise
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthExerciseType
import kotlinx.android.synthetic.main.activity_strength_exercise_editor.*

class StrengthExerciseEditor : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        const val EXERCISE_ID_KEY = "ENTRY_KEY"
        const val CHANGED_KEY = "CHANGED"
        const val DELETED_KEY = "DELETED"

        const val ADD_MODE = "ADD_MODE"
        const val EDIT_MODE = "EDIT_MODE"
    }

    private var editMode = false

    private var exercise: StrengthExercise? = null

    private var id: Long? = null
    private var name: String? = null
    private var type: StrengthExerciseType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strength_exercise_editor)

        val add = intent.getBooleanExtra(ADD_MODE, false)
        val edit = intent.getBooleanExtra(EDIT_MODE, false)
        if ((!add && !edit) || (add && edit)) throw RuntimeException("Specify either add or edit mode")
        editMode = edit

        intent.getLongExtra(EXERCISE_ID_KEY, -1).apply {
            id = if (this < 0L) null else this
        }

        AsyncTask.execute {
            fetchExercise()
            runOnUiThread {
                setupSpinner()
                setupNameTextField()
                setupTitle()
            }
        }

    }

    private fun setupTitle() {
        supportActionBar?.title = if (exercise == null) "Add exercise" else exercise?.name
    }

    private fun fetchExercise() {
        val exerciseId = id
        if (exerciseId != null && exerciseId > -1L) {
            exercise = GymBuddyRoomDataBase.strengthExerciseDao.get(exerciseId)
            id = exercise?.id
            name = exercise?.name
            type = exercise?.type
        }
    }

    private fun setupNameTextField() {
        exercise_name_text.setText(name)
        exercise_name_text.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                name = p0?.toString()
            }
        })

    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter<StrengthExerciseType>(this, android.R.layout.simple_spinner_item, StrengthExerciseType.values())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        strength_exercise_type_spinner.adapter = adapter

        val position = if (type == null) 0 else adapter.getPosition(type)
        strength_exercise_type_spinner.setSelection(position)
        type = strength_exercise_type_spinner.getItemAtPosition(position) as StrengthExerciseType?

        strength_exercise_type_spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View,
                                pos: Int, id: Long) {
        type = parent.getItemAtPosition(pos) as StrengthExerciseType?
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        type = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (editMode) {
            menuInflater.inflate(R.menu.weight_edit_menu, menu)
        } else {
            menuInflater.inflate(R.menu.add_entry_menu, menu)
        }
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
            if (!save()) return true

            val intent = Intent()
            intent.putExtra(EXERCISE_ID_KEY, id)
            intent.putExtra(CHANGED_KEY, true)
            setResult(Activity.RESULT_OK, intent)

            finish()
            return true
        }

        if (id == R.id.menu_item_delete) {
            val intent = Intent()
            intent.putExtra(EXERCISE_ID_KEY, id)
            intent.putExtra(DELETED_KEY, true)
            setResult(Activity.RESULT_OK, intent)

            AlertDialog.Builder(this)
                    .setTitle("Delete?")
                    .setMessage("This exercise will be removed permanently.")
                    .setPositiveButton(android.R.string.yes) { _, _ -> delete().run { finish() } }
                    .setNegativeButton(android.R.string.no, null).show()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun delete() {
        val exercise = exercise
        if (exercise != null) {
            AsyncTask.execute {
                GymBuddyRoomDataBase.strengthExerciseDao.deleteAll(exercise)
            }
        }
    }

    private fun save(): Boolean {
        val name = name
        val type = type
        if (name == null || type == null) return false

        var exercise = exercise
        if (exercise == null) {
            exercise = StrengthExercise(0, name, type)
            AsyncTask.execute {
                GymBuddyRoomDataBase.strengthExerciseDao.insertAll(exercise)
            }
        } else {
            exercise.name = name
            exercise.type = type
            AsyncTask.execute {
                GymBuddyRoomDataBase.strengthExerciseDao.updateAll(exercise)
            }
        }
        sendBroadcast(Intent("fi.anttonen.villematti.apps.gymbuddy.EXERCISE_CHANGED"))

        return true
    }

    abstract inner class CustomTextWatcher : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        abstract override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
    }
}
