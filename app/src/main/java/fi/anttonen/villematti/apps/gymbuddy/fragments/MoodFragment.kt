/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.fragments

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.R.id.imageButtonDissatisfied
import fi.anttonen.villematti.apps.gymbuddy.R.string.mood
import kotlinx.android.synthetic.main.fragment_mood.*
import kotlinx.android.synthetic.main.fragment_mood.view.*

private const val MOOD_VALUE = MoodFragment.NO_SELECTION


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MoodFragment.MoodFragmentListener] interface
 * to handle interaction events.
 * Use the [MoodFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MoodFragment : Fragment() {
    private lateinit var moodButtons: List<ImageButton>
    private var mood: String? = null
    private var listener: MoodFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mood = it.getString(MOOD_VALUE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mood, container, false)
        moodButtons = listOf(
                setupMoodButton(view.imageButtonVeryDissatisfied, VERY_DISSATISFIED),
                setupMoodButton(view.imageButtonDissatisfied, DISSATISFIED),
                setupMoodButton(view.imageButtonSatisfied, SATISFIED),
                setupMoodButton(view.imageButtonVerySatisfied, VERY_SATISFIED))

        return view
    }

    private fun setupMoodButton(button: ImageButton, mood: String): ImageButton {
        button.tag = mood
        setActivated(button, this.mood == mood)
        button.apply { setOnClickListener { onButtonPressed(this@apply) } }
        return button
    }

    private fun onButtonPressed(view: View) {
        val pressedMood = view.tag as String
        mood = if (mood == pressedMood) null else pressedMood
        for (button in moodButtons) setActivated(button, button.tag == mood)
        listener?.onMoodSelection(mood)
    }

    private fun setActivated(button: View, activated: Boolean) {
        button.isActivated = activated
        button.alpha = if (activated) 1.0f else .20f
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MoodFragmentListener) {
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
    interface MoodFragmentListener {
        fun onMoodSelection(mood: String?)
    }

    companion object {
        const val VERY_SATISFIED = "++"
        const val SATISFIED = "+"
        const val DISSATISFIED = "-"
        const val VERY_DISSATISFIED = "--"
        const val NO_SELECTION = "No selection"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param mood Parameter 1.
         * @return A new instance of fragment MoodFragment.
         */
        @JvmStatic
        fun newInstance(mood: String?) =
                MoodFragment().apply {
                    arguments = Bundle().apply {
                        putString(MOOD_VALUE, mood)
                    }
                }
    }
}
