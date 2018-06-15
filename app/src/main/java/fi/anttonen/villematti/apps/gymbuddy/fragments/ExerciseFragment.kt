/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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

private const val EXERCISE_ID = "Exercise id"

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
    private var exerciseId: Long = -1
    private var exercise: StrengthExercise? = null
    private var workout: StrengthWorkoutEntry? = null
    private var sets: MutableList<ExerciseSet> = mutableListOf()

    //private var listener: ExerciseFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            exerciseId = it.getLong(EXERCISE_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)
        view.add_set_button.setOnClickListener { onAddSetButtonClick() }
        AsyncTask.execute {
            workout = ViewModelProviders.of(activity!!).get(WorkoutEditViewModel::class.java).workout
            exercise = ViewModelProviders.of(activity!!).get(StrengthWorkoutViewModel::class.java).getExercise(exerciseId)
            activity?.runOnUiThread {
                view.exercise_name.text = exercise?.name
                sets.addAll(workout!!.sets.filter { it.exerciseId == exercise!!.id })
                sets.sort()
                sets.forEach { inflateSetView(it) }
            }
        }

        return view
    }

    fun onAddSetButtonClick() {
        // TODO add set fragment, compute sequence + init fields
        val nextSequence = if (sets.isEmpty()) 0 else sets.last().sequence + 1
        val set = ExerciseSet(0, workout!!.id, exercise!!.id, nextSequence)
        sets.add(set)
        inflateSetView(set)
    }

    fun inflateSetView(set: ExerciseSet) {
        val setView = layoutInflater.inflate(R.layout.exercise_set_view, sets_layout, false)
        setView.tag = set

        setView.weight_unit_label.text = when (UnitManager.Units.weightRatio) {
            UnitManager.WeightRatio.KG -> "kg"
            UnitManager.WeightRatio.LBS -> "lbs"
            else -> "unknown"
        }

        setView.reps_edit_text.setText(set.reps?.toString() ?: "")
        setView.weight_edit_text.setText(set.getWeightUI(2).toString())

        sets_layout.addView(setView)

        // TODO add text change listener, identify sets by tag
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*
        if (context is ExerciseFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
        */
    }

    override fun onDetach() {
        super.onDetach()
        //listener = null
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ExerciseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(exerciseId: Long) =
                ExerciseFragment().apply {
                    arguments = Bundle().apply {
                        putLong(EXERCISE_ID, exerciseId)
                    }
                }
    }
}
