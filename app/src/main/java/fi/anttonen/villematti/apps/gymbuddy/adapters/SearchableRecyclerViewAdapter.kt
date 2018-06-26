/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.misc.SearchableView
import fi.anttonen.villematti.apps.gymbuddy.misc.SearchableViewType
import kotlinx.android.synthetic.main.exercise_list_row.view.*

class SearchableRecyclerViewAdapter : RecyclerView.Adapter<SearchableRecyclerViewAdapter.SearchableViewHolder>() {
    private var allItems: List<SearchableView>? = null
    private var filteredItems: List<SearchableView>? = null
    lateinit var itemClickListener: OnItemClickListener
    private var query: String? = null

    fun updateItems(newData: List<SearchableView>?) {
        allItems = newData
        update()
    }

    fun updateQuery(newQuery: String?) {
        query = newQuery
        update()
    }

    private fun update() {
        val newFilteredList = applyFilterTo(allItems)
        DiffUtil.calculateDiff(EntryRowDiffCallback(newFilteredList, filteredItems), false).dispatchUpdatesTo(this)
        filteredItems = newFilteredList

    }

    private fun applyFilterTo(data: List<SearchableView>?): List<SearchableView>? {
        return data?.filter { it.matches(query) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchableViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val inflatedView = when (viewType) {
            SearchableViewType.CONTENT.ordinal -> layoutInflater.inflate(R.layout.exercise_list_row, parent, false)
            else -> layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false) //TODO AUXILIARY
        }
        return SearchableViewHolder(inflatedView)
    }

    override fun getItemCount(): Int = filteredItems?.size ?: 0

    override fun onBindViewHolder(holder: SearchableViewHolder, position: Int) {
        val item = filteredItems?.get(position)
        holder.bindEntry(item)
    }

    override fun getItemViewType(position: Int): Int {
        return filteredItems?.get(position)?.type()?.ordinal ?: -1
    }

    fun getListItemAtPosition(position: Int): SearchableView? {
        if (position < 0 || position >= filteredItems?.size ?: Int.MIN_VALUE) {
            return null
        }
        return filteredItems?.get(position)
    }

    inner class SearchableViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v

        init {
            v.setOnClickListener(this)
        }

        fun bindEntry(item: SearchableView?) {
            view.title_text.text = item?.title()
            view.subtitle_text.text = item?.subTitle()
            view.meta_text.text = item?.meta()
        }

        override fun onClick(view: View) = itemClickListener.onItemClick(itemView, adapterPosition, getListItemAtPosition(adapterPosition))
    }

    inner class EntryRowDiffCallback(private val newRows: List<SearchableView>?, private val oldRows: List<SearchableView>?) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRow = oldRows?.get(oldItemPosition)
            val newRow = newRows?.get(newItemPosition)
            return oldRow?.type() == newRow?.type()
        }

        override fun getOldListSize(): Int = oldRows?.size ?: 0

        override fun getNewListSize(): Int = newRows?.size ?: 0

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRow = oldRows?.get(oldItemPosition)
            val newRow = newRows?.get(newItemPosition)
            return oldRow == newRow
        }

    }

    /**
     * Click interface
     */
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, item: SearchableView?)
    }
}