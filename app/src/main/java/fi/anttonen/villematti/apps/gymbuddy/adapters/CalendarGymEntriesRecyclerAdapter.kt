/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.adapters

import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jjoe64.graphview.GridLabelRenderer
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.misc.ExerciseSetSummaryView
import fi.anttonen.villematti.apps.gymbuddy.misc.StringGenerator
import fi.anttonen.villematti.apps.gymbuddy.model.CalendarGymEntriesViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.database.GymBuddyRoomDataBase
import fi.anttonen.villematti.apps.gymbuddy.model.entity.*
import kotlinx.android.synthetic.main.cardio_entry_row.view.*
import kotlinx.android.synthetic.main.exercise_set_summary_view.view.*
import kotlinx.android.synthetic.main.strength_workout_entry_row.view.*
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
            EntryType.STRENGTH.ordinal -> layoutInflater.inflate(R.layout.strength_workout_entry_row, parent, false)
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
    inner class CalendarGymEntryHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener, LoadHistoryTaskCallback, LoadExerciseTaskCallback, LoadCardioExerciseTaskCallback {

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
            if (gymEntry is StrengthWorkoutEntry) {
                view.strength_workout_entry_type.text = gymEntry.getEntryType().displayName
                bindStrengthWorkoutEntry(gymEntry)
            }
        }

        private fun bindStrengthWorkoutEntry(workout: StrengthWorkoutEntry) {
            // Clear old views
            view.exercise_summary_layout.removeAllViews()

            val exerciseSequenceMap = workout.getExerciseSequenceMap()
            // TODO cache worth it? If so, must be refactored to higher level than this method
            //val exerciseCache = mutableListOf<StrengthExercise>()
            for (sequence in exerciseSequenceMap.keys.sorted()) {
                val summaryView = ExerciseSetSummaryView(view.context)
                summaryView.tag = sequence
                view.exercise_summary_layout.addView(summaryView)

                val sets = exerciseSequenceMap[sequence]!!
                val exerciseId = sets.first().exerciseId
                LoadExerciseTask(sequence, sets, viewModel, this).execute(exerciseId)
            }
        }

        private fun bindCardioEntry(cardioEntry: CardioEntry) {
            val distanceString = cardioEntry.getHumanReadableDistance() ?: "-"
            val durationString = cardioEntry.getHumanReadableDuration() ?: "-"
            val avgSpeedString = cardioEntry.avgSpeedText() ?: "-"

            if (cardioEntry.cardioTypeId == null) {
                view.cardio_entry_type.text = "Cardio"
            } else {
                LoadCardioExerciseTask(cardioEntry, this).execute(cardioEntry.cardioTypeId)
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
         * LoadHistoryTaskCallback override to get execution back on UI thread when database query has been done
         */
        override fun loadHistoryTaskCompleted(gymEntry: WeightEntry, result: List<WeightEntry>?) {
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

        override fun loadExerciseTaskCompleted(summaryViewTag: Int, sets: List<ExerciseSet>, result: StrengthExercise?) {
            val summaryView = view.exercise_summary_layout.findViewWithTag<ExerciseSetSummaryView>(summaryViewTag)
            val exercise = result!!

            summaryView.exercise_name_text.text = exercise.name
            summaryView.exercise_type_text.text = exercise.type.description

            val summaryStrings = StringGenerator.generateHumanReadableSetsSummary(sets)
            summaryView.set_summary_layout.removeAllViews()
            for (summary in summaryStrings) {
                val tv = TextView(view.context)
                tv.text = summary
                summaryView.set_summary_layout.addView(tv)
            }
        }

        override fun loadCardioExerciseTaskCompleted(entry: CardioEntry, result: CardioType?) {
            view.cardio_entry_type.text = result?.name
        }
    }


    //////////////////////

    class LoadHistoryTask(private val entry: WeightEntry, private val viewModel: CalendarGymEntriesViewModel, private val callback: LoadHistoryTaskCallback) : AsyncTask<LocalDate, Int, List<WeightEntry>>() {
        override fun doInBackground(vararg date: LocalDate?): List<WeightEntry> {
            if (date.isEmpty() || date.first() == null) return emptyList()
            return viewModel.getWeightEntryHistoryForDate(date.first()!!)
        }

        override fun onPostExecute(result: List<WeightEntry>?) {
            super.onPostExecute(result)
            callback.loadHistoryTaskCompleted(entry, result)
        }
    }

    interface LoadHistoryTaskCallback {
        fun loadHistoryTaskCompleted(gymEntry: WeightEntry, result: List<WeightEntry>?)
    }

    //////////////////////

    class LoadExerciseTask(private val summaryViewTag: Int, private val sets: List<ExerciseSet>, private val viewModel: CalendarGymEntriesViewModel, private val callback: LoadExerciseTaskCallback) : AsyncTask<Long, Int, StrengthExercise>() {
        override fun doInBackground(vararg exerciseId: Long?): StrengthExercise {
            return viewModel.getStrengthExercise(exerciseId.first()!!)
        }

        override fun onPostExecute(result: StrengthExercise?) {
            super.onPostExecute(result)
            callback.loadExerciseTaskCompleted(summaryViewTag, sets, result)
        }
    }

    interface LoadExerciseTaskCallback {
        fun loadExerciseTaskCompleted(summaryViewTag: Int, sets: List<ExerciseSet>, result: StrengthExercise?)
    }

    //////////////////////

    class LoadCardioExerciseTask(private val entry: CardioEntry, private val callback: LoadCardioExerciseTaskCallback) : AsyncTask<Long, Int, CardioType>() {
        override fun doInBackground(vararg id: Long?): CardioType {
            return GymBuddyRoomDataBase.cardioTypeDao.get(id.first()!!)
        }

        override fun onPostExecute(result: CardioType?) {
            super.onPostExecute(result)
            callback.loadCardioExerciseTaskCompleted(entry, result)
        }
    }

    interface LoadCardioExerciseTaskCallback {
        fun loadCardioExerciseTaskCompleted(entry: CardioEntry, result: CardioType?)
    }

    //////////////////////

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