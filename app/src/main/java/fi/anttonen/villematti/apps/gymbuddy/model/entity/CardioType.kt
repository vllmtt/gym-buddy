/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import fi.anttonen.villematti.apps.gymbuddy.misc.SearchableView
import fi.anttonen.villematti.apps.gymbuddy.misc.SearchableViewType

@Entity(tableName = "cardio_type")
data class CardioType(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
                      @ColumnInfo var name: String) : SearchableView {

    @ColumnInfo
    var usageCount: Long = 0

    override fun matches(query: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun title() = name
    override fun subTitle(): String? = null
    override fun meta(): String? = null

    override fun relevanceCount(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun type() = SearchableViewType.CONTENT

    override fun toString(): String {
        return name
    }

    fun toDbString(): String {
        return "$id;$name"
    }

    companion object {
        val DEFAULT_CARDIO_TYPES = mutableListOf<CardioType>().apply {
            val names = arrayOf("Biking", "Running", "Walking")
            for (i in names.indices) this.add(CardioType(i.toLong(), names[i]))
        }

        fun parse(value: String): CardioType {
            val parts = value.split(";")
            if (parts.size > 1) {
                val id = parts[0].toLong()
                val name = parts[1]
                return CardioType(id, name)
            }
            throw RuntimeException("Couldn't parse string: '$value' to CardioType")
        }
    }
}