/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.adapters.SearchableRecyclerViewAdapter
import fi.anttonen.villematti.apps.gymbuddy.misc.SearchableView
import kotlinx.android.synthetic.main.fragment_searchable_recycler_view.*
import kotlinx.android.synthetic.main.fragment_searchable_recycler_view.view.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val INITIAL_QUERY = "INITIAL_QUERY"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SearchableRecyclerViewFragment.SearchableRecyclerViewFragmentListener] interface
 * to handle interaction events.
 * Use the [SearchableRecyclerViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SearchableRecyclerViewFragment : Fragment(), SearchableRecyclerViewAdapter.OnItemClickListener {

    private var initialQuery: String? = null
    private var listener: SearchableRecyclerViewFragmentListener? = null
    private var adapter: SearchableRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            initialQuery = it.getString(INITIAL_QUERY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_searchable_recycler_view, container, false)
        view.recycler_view.adapter = adapter
        view.recycler_view.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchableRecyclerViewFragmentListener) {
            listener = context
            adapter = SearchableRecyclerViewAdapter()
            adapter?.itemClickListener = this
        } else {
            throw RuntimeException(context.toString() + " must implement SearchableRecyclerViewFragmentListener")
        }
    }

    fun updateViewItems(newItems: List<SearchableView>) {
        adapter?.updateItems(newItems)
    }

    fun updateQuery(newQuery: String?) {
        adapter?.updateQuery(newQuery)
    }

    override fun onItemClick(view: View, position: Int, item: SearchableView?) {
        listener?.onItemSelection(item)
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
    interface SearchableRecyclerViewFragmentListener {
        fun onItemSelection(selectedItem: SearchableView?)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param initialQuery Parameter 1.
         * @return A new instance of fragment SearchableRecyclerViewFragment.
         */
        @JvmStatic
        fun newInstance(initialQuery: String) =
                SearchableRecyclerViewFragment().apply {
                    arguments = Bundle().apply {
                        putString(INITIAL_QUERY, initialQuery)
                    }
                }
    }

}
