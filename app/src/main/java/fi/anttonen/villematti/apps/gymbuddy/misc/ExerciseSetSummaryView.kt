/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.misc

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import fi.anttonen.villematti.apps.gymbuddy.R

class ExerciseSetSummaryView : LinearLayout {

    init {
        inflate(context, R.layout.exercise_set_summary_view, this)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context) : super(context)
}