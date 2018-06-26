/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy

import android.arch.lifecycle.ViewModel
import fi.anttonen.villematti.apps.gymbuddy.misc.WorkoutCoordinator

class EditWorkoutViewModel : ViewModel() {
    var workoutCoordinator: WorkoutCoordinator? = null
}