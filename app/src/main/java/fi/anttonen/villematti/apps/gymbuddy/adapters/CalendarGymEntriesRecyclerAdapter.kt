package fi.anttonen.villematti.apps.gymbuddy.adapters

import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jjoe64.graphview.GridLabelRenderer
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.CalendarGymEntriesViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.entity.CardioEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.WeightEntry
import fi.anttonen.villematti.apps.gymbuddy.model.entity.EntryType
import fi.anttonen.villematti.apps.gymbuddy.model.entity.GymEntry
import kotlinx.android.synthetic.main.cardio_entry_row.view.*
import kotlinx.android.synthetic.main.weight_entry_row.view.*
import org.joda.time.LocalDate

class CalendarGymEntriesRecyclerAdapter(var gymEntries: List<GymEntry>?, val viewModel: CalendarGymEntriesViewModel) : RecyclerView.Adapter<CalendarGymEntriesRecyclerAdapter.CalendarGymEntryHolder>() {

    lateinit var itemClickListener: OnItemClickListener

    fun updateGymEntries(newData: List<GymEntry>?) {
        DiffUtil.calculateDiff(EntryRowDiffCallback(newData, gymEntries), false).dispatchUpdatesTo(this)
        gymEntries = newData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarGymEntryHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView: View = when (viewType) {
            EntryType.WEIGHT.ordinal -> layoutInflater.inflate(R.layout.weight_entry_row, parent, false)
            EntryType.CARDIO.ordinal -> layoutInflater.inflate(R.layout.cardio_entry_row, parent, false)
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
    fun getListItemAtPosition(position: Int): GymEntry? {
        if (position < 0 || position >= gymEntries?.size ?: Int.MIN_VALUE) {
            return null
        }
        return gymEntries?.get(position)
    }


    /**
     * Gym entry view holder
     */
    inner class CalendarGymEntryHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener, TaskCallback {

        private var view: View = v

        init {
            v.setOnClickListener(this)
        }


        override fun onClick(view: View) = itemClickListener.onItemClick(itemView, adapterPosition, getListItemAtPosition(adapterPosition))


        /**
         * Binds an entry to the view holder
         */
        fun bindEntry(gymEntry: GymEntry?) {
            if (gymEntry is WeightEntry) {
                view.weight_entry_type.text = gymEntry.getEntryType().displayName
                bindWeightEntry(gymEntry)
            }
            if (gymEntry is CardioEntry) {
                view.cardio_entry_type.text = gymEntry.getEntryType().displayName
                bindCardioEntry(gymEntry)
            }
        }


        private fun bindCardioEntry(cardioEntry: CardioEntry) {
            val cardioTypeString = cardioEntry.cardioType?.toString()
            val distanceString = cardioEntry.getHumanReadableDistance() ?: "-"
            val durationString = cardioEntry.getHumanReadableDuration() ?: "-"
            val avgSpeedString = cardioEntry.avgSpeedText() ?: "-"

            if (cardioTypeString != null) {
                view.cardio_entry_type.text = cardioTypeString
            }

            val distanceVisibility = /*if (distanceString == null) View.GONE else*/ View.VISIBLE
            view.distance_text.visibility = distanceVisibility
            view.distance_label.visibility = distanceVisibility
            view.distance_text.text = distanceString

            val durationVisibility = /*if (durationString == null) View.GONE else*/ View.VISIBLE
            view.duration_text.visibility = durationVisibility
            view.duration_label.visibility = durationVisibility
            view.duration_text.text = durationString

            val avgSpeedVisibility = /*if (distanceString == null || durationString == null) View.INVISIBLE else*/ View.VISIBLE
            view.average_speed_label.visibility = avgSpeedVisibility
            view.average_speed_text.visibility = avgSpeedVisibility
            view.average_speed_text.text = avgSpeedString
        }


        /**
         * Binds a weight entry to the view holder
         */
        private fun bindWeightEntry(gymEntry: WeightEntry) {
            view.weight_text.text = gymEntry.getWeightUI(1).toString()
            view.weight_unit_text.text = WeightEntry.unitString
            view.weight_graph.removeAllSeries()

            view.weight_graph.gridLabelRenderer.isHorizontalLabelsVisible = false
            view.weight_graph.gridLabelRenderer.isVerticalLabelsVisible = false
            view.weight_graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE

            view.weight_graph.gridLabelRenderer.numHorizontalLabels = 0
            view.weight_graph.gridLabelRenderer.numVerticalLabels = 0

            LoadHistoryTask(gymEntry, viewModel, this).execute(gymEntry.date)

        }

        /**
         * TaskCallback override to get execution back on UI thread when database query has benn done
         */
        override fun completed(gymEntry: WeightEntry, result: List<WeightEntry>?) {
            if (result != null && result.size > 1) {
                view.weight_graph.visibility = View.VISIBLE
                view.noWeightHistoryLabel.visibility = View.GONE

                val series = WeightEntry.dataPointSeriesFrom(result)
                series.color = ContextCompat.getColor(view.context, R.color.colorAccent)
                series.isDrawDataPoints = false
                series.thickness = 4
                view.weight_graph.addSeries(series)

                // TODO weight graph styling to onCreateViewHolder()

                val minX = LocalDate(result.last().date).toDate().time
                val maxX = LocalDate(result.first().date).toDate().time
                view.weight_graph.viewport.setMinX(minX.toDouble())
                view.weight_graph.viewport.setMaxX(maxX.toDouble())
                view.weight_graph.viewport.isXAxisBoundsManual = true

                val lowY = series.lowestValueY
                val highY = series.highestValueY
                if (lowY != highY) {
                    view.weight_graph.viewport.setMinY(lowY)
                    view.weight_graph.viewport.setMaxY(highY)
                    view.weight_graph.viewport.isYAxisBoundsManual = true
                }

                view.weight_graph.gridLabelRenderer.setHumanRounding(true)

            } else {
                //view.weight_graph.visibility = View.GONE
                view.noWeightHistoryLabel.visibility = View.VISIBLE
            }

        }
    }

    class LoadHistoryTask(private val entry: WeightEntry, private val viewModel: CalendarGymEntriesViewModel, private val callback: TaskCallback) : AsyncTask<LocalDate, Int, List<WeightEntry>>() {
        override fun doInBackground(vararg date: LocalDate?): List<WeightEntry> {
            if (date.isEmpty() || date.first() == null) return emptyList()
            return viewModel.getWeightEntryHistoryForDate(date.first()!!)
        }

        override fun onPostExecute(result: List<WeightEntry>?) {
            super.onPostExecute(result)
            callback.completed(entry, result)
        }
    }

    interface TaskCallback {
        fun completed(gymEntry: WeightEntry, result: List<WeightEntry>?)
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