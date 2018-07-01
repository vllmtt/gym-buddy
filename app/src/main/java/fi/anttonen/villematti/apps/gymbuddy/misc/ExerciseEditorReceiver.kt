/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.misc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ExerciseEditorReceiver : BroadcastReceiver() {

    var listener: ExerciseEditListener? = null

    override fun onReceive(p0: Context?, p1: Intent?) {
        listener?.exerciseSettingsChanged()
    }

    interface ExerciseEditListener {
        fun exerciseSettingsChanged()
    }
}