package fi.anttonen.villematti.apps.gymbuddy.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jjoe64.graphview.GridLabelRenderer
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.EntryType
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import kotlinx.android.synthetic.main.weight_entry_row.view.*
import org.joda.time.LocalDate

class CalendarGymEntriesRecyclerAdapter(var gymEntries: List<GymEntry>?) : RecyclerView.Adapter<CalendarGymEntriesRecyclerAdapter.CalendarGymEntryHolder>() {


    lateinit var itemClickListener: OnItemClickListener

    fun updateGymEntries(newData: List<GymEntry>?) {
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
        return gymEntries?.size ?: 0
    }

    override fun onBindViewHolder(holder: CalendarGymEntryHolder, position: Int) {
        val gymEntry = gymEntries?.get(position)
        holder.bindEntry(gymEntry)
    }

    override fun getItemViewType(position: Int): Int {
        return gymEntries?.get(position)?.getEntryType()?.ordinal ?: -1
    }


    /**
     * Gets entry at a position either from filtered or unfiltered list
     */
    fun getListItemAtPosition(position: Int) = gymEntries?.get(position)


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
        fun bindEntry(gymEntry: GymEntry?) {
            view.entry_type.text = gymEntry?.getEntryType()?.displayName
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

            /*
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
            */
        }

    }

    inner class EntryRowDiffCallback(private val newRows: List<GymEntry>?, private val oldRows: List<GymEntry>?) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRow = oldRows?.get(oldItemPosition)
            val newRow = newRows?.get(newItemPosition)
            return oldRow?.getEntryType() == newRow?.getEntryType()
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
        fun onItemClick(view: View, position: Int, entry: GymEntry?)
    }
}