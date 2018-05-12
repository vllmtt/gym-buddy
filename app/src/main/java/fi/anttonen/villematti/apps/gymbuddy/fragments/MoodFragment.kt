package fi.anttonen.villematti.apps.gymbuddy.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fi.anttonen.villematti.apps.gymbuddy.R
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

        view.imageButtonThumbUp.tag = THUMB_UP
        setActivated(view.imageButtonThumbUp, mood == THUMB_UP)
        view.imageButtonThumbUp.apply { setOnClickListener { onButtonPressed(this) } }

        view.imageButtonThumbDown.tag = THUMB_DOWN
        setActivated(view.imageButtonThumbDown, mood == THUMB_DOWN)
        view.imageButtonThumbDown.apply { setOnClickListener { onButtonPressed(this) } }

        return view
    }

    private fun onButtonPressed(view: View) {
        if (view.tag == THUMB_UP) {
            setActivated(imageButtonThumbDown, false)
            setActivated(imageButtonThumbUp, !imageButtonThumbUp.isActivated)
            listener?.onMoodSelection(if (!imageButtonThumbUp.isActivated) THUMB_UP else null)
        }
        if  (view.tag == THUMB_DOWN) {
            setActivated(imageButtonThumbUp, false)
            setActivated(imageButtonThumbDown, !imageButtonThumbDown.isActivated)
            listener?.onMoodSelection(if (!imageButtonThumbDown.isActivated) THUMB_UP else null)
        }
    }

    fun setActivated(button: View, activated: Boolean) {
        button.isActivated = activated
        button.alpha = if (activated) 1.0f else .38f
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
        const val THUMB_UP = "Thumb up"
        const val THUMB_DOWN = "Thumb down"
        const val NO_SELECTION = "No selection"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment MoodFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                MoodFragment().apply {
                    arguments = Bundle().apply {
                        putString(MOOD_VALUE, param1)
                    }
                }
    }
}
