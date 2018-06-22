/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.StrengthWorkoutViewModel
import fi.anttonen.villematti.apps.gymbuddy.WorkoutEditViewModel
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import fi.anttonen.villematti.apps.gymbuddy.model.entity.ExerciseSet
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthExercise
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthWorkoutEntry
import kotlinx.android.synthetic.main.exercise_set_view.view.*
import kotlinx.android.synthetic.main.fragment_exercise.*
import kotlinx.android.synthetic.main.fragment_exercise.view.*
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import fi.anttonen.villematti.apps.gymbuddy.misc.WorkoutCoordinator


private const val EXERCISE_SEQUENCE = "Exercise sequence"
private const val EXERCISE_ID = "Exercise id"
private const val EDIT_MODE = "Edit mode"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ExerciseFragment.ExerciseFragmentListener] interface
 * to handle interaction events.
 * Use the [ExerciseFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ExerciseFragment : Fragment() {
    var exerciseSequence = -1
    var exerciseId: Long = -1
    var exercise: StrengthExercise? = null
    private lateinit var workoutCoordinator: WorkoutCoordinator

    private var editMode = false
    private var listener: ExerciseFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            exerciseSequence = it.getInt(EXERCISE_SEQUENCE)
            exerciseId = it.getLong(EXERCISE_ID)
            editMode = it.getBoolean(EDIT_MODE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)

        view.add_set_button.setOnClickListener { onAddSetButtonClick() }

        view.delete_exercise_button.visibility = if (editMode) View.VISIBLE else View.INVISIBLE
        view.delete_exercise_button.setOnClickListener {
            listener?.deleteExercise(this)
        }

        view.tag = this
        AsyncTask.execute {
            workoutCoordinator = ViewModelProviders.of(activity!!).get(WorkoutEditViewModel::class.java).workoutCoordinator!!
            exercise = ViewModelProviders.of(activity!!).get(StrengthWorkoutViewModel::class.java).getExercise(exerciseId)
            activity?.runOnUiThread {
                view.exercise_name.text = exercise?.name
                view.exercise_type.text = exercise?.type?.description

                val sets = workoutCoordinator.sequenceSetsMap[exerciseSequence]!!
                if (sets.isNotEmpty()) {
                    sets.forEach { inflateSetView(it, inflater, view) }
                } else {
                    // Force add one set
                    onAddSetButtonClick()
                }

                listener?.exerciseViewAdded(view, exerciseSequence)
            }
        }

        return view
    }

    private fun onAddSetButtonClick() {
        val set = ExerciseSet(0, workoutCoordinator.workout.id, exercise!!.id, 0, exerciseSequence)
        workoutCoordinator.addSet(set)
        inflateSetView(set)
    }

    private fun onDeleteSetButtonClick(set: ExerciseSet) {
        workoutCoordinator.removeSet(set)
        val setView = sets_layout.findViewWithTag<View>(set)
        sets_layout.removeView(setView)

        // Delete exercise if no sets are left
        if (sets_layout.childCount < 1) {
            listener?.deleteExercise(this)
        }
    }

    private fun inflateSetView(set: ExerciseSet, inflater: LayoutInflater = layoutInflater, exerciseView: View? = view) {
        val setView = inflater.inflate(R.layout.exercise_set_view, sets_layout, false)
        setView.tag = set

        setView.weight_unit_label.text = when (UnitManager.Units.weightRatio) {
            UnitManager.WeightRatio.KG -> "kg"
            UnitManager.WeightRatio.LBS -> "lbs"
            else -> "unknown"
        }

        setView.reps_edit_text.setText(set.reps?.toString() ?: "")
        setView.weight_edit_text.setText(set.getWeightUI(2)?.toString() ?: "")

        setView.reps_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isNumberBetween(0.0, 9999.0, p0, true, null, null)) {
                    set.reps = if (p0.isNullOrEmpty()) null else p0?.toString()?.toInt()
                    if (p0.toString()?.length > 1) setView.weight_edit_text.requestFocus()
                }
            }
        })
        setView.weight_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isNumberBetween(0.0, 99999.0, p0, true, null, null)) {
                    set.setWeight(if (p0.isNullOrEmpty()) null else p0?.toString()?.toDouble(), true)
                }
            }
        })

        setView.delete_set_button.visibility = if (editMode) View.VISIBLE else View.INVISIBLE
        setView.delete_set_button.setOnClickListener { onDeleteSetButtonClick(set) }

        exerciseView?.sets_layout?.addView(setView)
    }

    fun setEditMode(enabled: Boolean, exerciseView: View? = view) {
        editMode = enabled
        val count = view?.sets_layout?.childCount ?: 0
        for (i in 0 until count) {
            val child = sets_layout.getChildAt(i)
            child.delete_set_button.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
        }
        view?.delete_exercise_button?.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
    }

    private fun isNumberBetween(min: Double?, max: Double?, string: CharSequence?, isEmptyValid: Boolean, errorMessage: String?, errorDestination: TextInputLayout?): Boolean {
        errorDestination?.error = null
        val text = string?.toString() ?: ""

        if (text.isEmpty()) {
            if (!isEmptyValid) errorDestination?.error = errorMessage
            return isEmptyValid
        }

        try {
            val n = text.toDouble()
            if ((min != null && n < min) || (max != null && n > max)) {
                errorDestination?.error = errorMessage
                return false
            }
            return true
        } catch (e: NumberFormatException) {
            errorDestination?.error = errorMessage
        }

        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ExerciseFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement ExerciseFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface ExerciseFragmentListener {
        fun exerciseViewAdded(view: View, exerciseSequence: Int)
        fun deleteExercise(sender: ExerciseFragment)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ExerciseFragment.
         */
        @JvmStatic
        fun newInstance(exerciseSequence: Int, exerciseId: Long, editMode: Boolean) =
                ExerciseFragment().apply {
                    arguments = Bundle().apply {
                        putInt(EXERCISE_SEQUENCE, exerciseSequence)
                        putLong(EXERCISE_ID, exerciseId)
                        putBoolean(EDIT_MODE, editMode)
                    }
                }
    }
}
