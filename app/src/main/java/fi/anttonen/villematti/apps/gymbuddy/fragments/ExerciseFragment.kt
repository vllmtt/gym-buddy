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
import fi.anttonen.villematti.apps.gymbuddy.model.entity.ExerciseSet
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthExercise
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthWorkoutEntry
import kotlinx.android.synthetic.main.fragment_exercise.view.*
import java.util.*

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

    private var listener: ExerciseFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            exerciseId = it.getLong(EXERCISE_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)
        workout = ViewModelProviders.of(this).get(WorkoutEditViewModel::class.java).workout
        AsyncTask.execute {
            exercise = ViewModelProviders.of(this).get(StrengthWorkoutViewModel::class.java).getExercise(exerciseId)
            activity?.runOnUiThread {
                if (workout != null && exercise != null) {
                    view.exercise_name.text = exercise?.name
                    sets.addAll(workout!!.sets.filter { it.exerciseId == exercise!!.id })
                    for (set in sets) {
                        // TODO add set fragment, fill with data
                    }
                }
            }
        }

        return view
    }

    fun onAddSetButtonClick(view: View) {
        // TODO add set fragment
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ExerciseFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface ExerciseFragmentListener {
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
