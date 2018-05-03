package fi.anttonen.villematti.apps.gymbuddy.control

import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
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
import kotlinx.android.synthetic.main.activity_weight_entry_detail.*
import kotlinx.android.synthetic.main.weight_entry_row.view.*
import org.joda.time.LocalDate
import java.util.*

class CalendarGymEntriesRecyclerAdapter : RecyclerView.Adapter<CalendarGymEntriesRecyclerAdapter.CalendarGymEntryHolder>() {

    private var gymEntries: List<GymEntry> = listOf()
    lateinit var itemClickListener: OnItemClickListener

    fun updateGymEntries(date: LocalDate?) {
        val newData = if (date == null) listOf() else DataSource.DATA_SOURCE!!.getGymEntries(date)
        DiffUtil.calculateDiff(EntryRowDiffCallback(newData, gymEntries), false).dispatchUpdatesTo(this)
        gymEntries = newData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarGymEntryHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView: View = when (viewType) {
            EntryType.WEIGHT.ordinal -> layoutInflater.inflate(R.layout.weight_entry_row, parent, false)
            else -> layoutInflater.inflate(R.layout.weight_entry_row, parent, false) //TODO implement other entry types
        }

        return CalendarGymEntryHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return gymEntries.size
    }

    override fun onBindViewHolder(holder: CalendarGymEntryHolder, position: Int) {
        val gymEntry = gymEntries[position]
        holder.bindEntry(gymEntry)
    }

    override fun getItemViewType(position: Int): Int {
        return gymEntries[position].getEntryType().ordinal
    }


    /**
     * Gets entry at a position either from filtered or unfiltered list
     */
    fun getListItemAtPosition(position: Int) = gymEntries[position]


    /**
     * Gym entry view holder
     */
    inner class CalendarGymEntryHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

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

            val data = DataSource.DATA_SOURCE!!.getGymEntriesBefore(gymEntry, 5, EntryType.WEIGHT) as List<WeightEntry>
            if (data.size > 1) {
                view.weight_graph.visibility = View.VISIBLE
                val series = gymEntry.dataPointSeriesFrom(data)
                series.color = ContextCompat.getColor(view.context, R.color.colorAccent)
                series.isDrawDataPoints = false
                series.thickness = 4
                view.weight_graph.addSeries(series)

                // TODO weight graph styling to onCreateViewHolder()
                view.weight_graph.gridLabelRenderer.isHorizontalLabelsVisible = false
                view.weight_graph.gridLabelRenderer.isVerticalLabelsVisible = false
                view.weight_graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE

                view.weight_graph.viewport.setMinX(LocalDate(data.last().date).toDate().time.toDouble())
                view.weight_graph.viewport.setMaxX(LocalDate(data.first().date).toDate().time.toDouble())
                view.weight_graph.viewport.isXAxisBoundsManual = true

                view.weight_graph.viewport.setMinY(series.lowestValueY)
                view.weight_graph.viewport.setMaxY(series.highestValueY)
                view.weight_graph.viewport.isYAxisBoundsManual = true

                view.weight_graph.gridLabelRenderer.setHumanRounding(false)
            } else {
                view.weight_graph.visibility = View.GONE
            }
        }

    }

    inner class EntryRowDiffCallback(private val newRows: List<GymEntry>, private val oldRows: List<GymEntry>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRow = oldRows[oldItemPosition]
            val newRow = newRows[newItemPosition]
            return oldRow.getEntryType() == newRow.getEntryType()
        }

        override fun getOldListSize(): Int = oldRows.size

        override fun getNewListSize(): Int = newRows.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRow = oldRows[oldItemPosition]
            val newRow = newRows[newItemPosition]
            return oldRow == newRow
        }

    }

    /**
     * Click interface
     */
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, entry: GymEntry)
    }
}