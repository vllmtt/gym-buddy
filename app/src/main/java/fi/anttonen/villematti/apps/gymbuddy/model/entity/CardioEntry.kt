package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import fi.anttonen.villematti.apps.gymbuddy.misc.roundToDecimalPlaces
import org.joda.time.Duration
import org.joda.time.LocalDate

@Entity(tableName = "cardio_entry")
class CardioEntry(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
                  @ColumnInfo(name = "date") var date: LocalDate,
                  @ColumnInfo(name = "distance") private var _distance: Double,
                  @ColumnInfo(name = "duration") var duration: Duration)  : GymEntry {

    @ColumnInfo(name = "mood")
    var mood: String? = null

    var distance: Double
        get() = _distance.roundToDecimalPlaces(1)
        set(value) {
            _distance = value
        }

    override fun getEntryType(): EntryType = EntryType.CARDIO

    override fun getEntryId(): Long = id

    override fun getEntryDate(): LocalDate = date

    override fun getEntryMood(): String? = mood

    override fun updateValuesFrom(entry: GymEntry) {
        if (entry is CardioEntry) {
            date = entry.date
            distance = entry.distance
            duration = entry.duration
            mood = entry.mood
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is CardioEntry) {
            return other.id == this.id && other.date == this.date && other.distance == this.distance && other.duration == this.duration && other.mood == this.mood
        }
        return false
    }

    override fun clone(): GymEntry {
        val clone = CardioEntry(this.id, LocalDate(this.date), this.distance, this.duration)
        clone.mood = this.mood
        return clone
    }
}