package fi.anttonen.villematti.apps.gymbuddy.control

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jjoe64.graphview.GridLabelRenderer
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.DataSource

import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.EntryType
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymEntry
import kotlinx.android.synthetic.main.weight_entry_row.view.*

/**
 * Created by vma on 25/04/2018.
 */
class GymEntriesRecyclerAdapter()  : RecyclerView.Adapter<GymEntriesRecyclerAdapter.GymEntryHolder>() {

    private val gymEntries = DataSource.DATA_SOURCE.getGymEntries()
    private val filteredGymEntries = gymEntries
    private var filtering = false
    lateinit var itemClickListener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymEntryHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView: View = when (viewType) {
            EntryType.WEIGHT.ordinal -> layoutInflater.inflate(R.layout.weight_entry_row, parent, false)
            else -> layoutInflater.inflate(R.layout.weight_entry_row, parent, false) //TODO implement other entry types
        }

        return GymEntryHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return if (filtering) filteredGymEntries.size else gymEntries.size
    }

    override fun onBindViewHolder(holder: GymEntryHolder, position: Int) {
        val gymEntry = if (filtering) filteredGymEntries[position] else gymEntries[position]
        holder.bindEntry(gymEntry)
    }

    override fun getItemViewType(position: Int): Int {
        return if (filtering)  filteredGymEntries[position].getEntryType().ordinal else gymEntries[position].getEntryType().ordinal
    }


    /**
     * Gets entry at a position either from filtered or unfiltered list
     */
    fun getListItemAtPosition(position: Int) = if (filtering) filteredGymEntries[position] else gymEntries[position]


    /**
     * Gym entry view holder
     */
    inner class GymEntryHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v

        init {
            v.setOnClickListener(this)
        }


        override fun onClick(view: View) = itemClickListener.onItemClick(itemView, adapterPosition, getListItemAtPosition(adapterPosition))


        /**
         * Binds an entry to the view holder
         */
        fun bindEntry(gymEntry: GymEntry) {
            view.entry_type.text = gymEntry.getEntryType().displayName
            if (gymEntry is WeightEntry) {
                bindWeightEntry(gymEntry)
            }
        }


        /**
         * Binds a weight entry to the view holder
         */
        private fun bindWeightEntry(gymEntry: WeightEntry) {
            view.weight_text.text = gymEntry.weight.toString()
            view.weight_unit_text.text = gymEntry.getUnitString()
            view.weight_graph.removeAllSeries()

            val data = DataSource.DATA_SOURCE.getGymEntriesBefore(gymEntry, 5, EntryType.WEIGHT) as List<WeightEntry>
            if (data.size > 1) {
                view.weight_graph.visibility = View.VISIBLE
                val series = gymEntry.dataPointSeriesFrom(data)
                series.color = ContextCompat.getColor(view.context, R.color.colorAccent)
                series.isDrawDataPoints = false
                series.thickness = 4
                view.weight_graph.addSeries(series)
            } else {
                view.weight_graph.visibility = View.GONE
            }

            // TODO weight graph styling to onCreateViewHolder()
            view.weight_graph.gridLabelRenderer.isHorizontalLabelsVisible = false
            view.weight_graph.gridLabelRenderer.isVerticalLabelsVisible = false
            view.weight_graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
        }

    }

    /**
     * Click interface
     */
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, entry: GymEntry)
    }
}