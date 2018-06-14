/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy

import android.arch.lifecycle.ViewModel
import fi.anttonen.villematti.apps.gymbuddy.model.entity.StrengthWorkoutEntry

class WorkoutEditViewModel : ViewModel() {
    var workout: StrengthWorkoutEntry? = null
}