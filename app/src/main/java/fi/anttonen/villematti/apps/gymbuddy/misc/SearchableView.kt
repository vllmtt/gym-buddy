/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.misc

interface SearchableView {
    fun matches(query: String?): Boolean
    fun title(): String
    fun subTitle(): String
    fun relevanceCount(): Long
    fun type(): SearchableViewType
}

enum class SearchableViewType {
    CONTENT,
    AUXILIARY
}