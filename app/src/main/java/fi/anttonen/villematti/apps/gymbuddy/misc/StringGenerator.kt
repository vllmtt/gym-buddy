/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.misc

import fi.anttonen.villematti.apps.gymbuddy.model.entity.ExerciseSet

class StringGenerator {
    companion object {

        //TODO
        fun generateHumanReadableSetsSummary(sets: List<ExerciseSet>) : List<String> {
            val texts = mutableListOf<String>()

            val weightLabel = when (UnitManager.Units.weightRatio) {
                UnitManager.WeightRatio.KG -> "kg"
                UnitManager.WeightRatio.LBS -> "lbs"
                else -> "unknown"
            }

            //var sb = StringBuilder()

            var i = 0
            while (i < sets.size) {
                val set = sets[i]
                //val nextSet = if (i + 1 < sets.size) sets[i + 1] else null
                //if (nextSet != null) {
                //
                //} else {
                //
                //}
                if (set.getWeight() != null && set.reps != null) {
                    val weight = set.getWeightUI(2)
                    texts.add("• ${set.reps} × $weight$weightLabel")
                } else if (set.getWeight() != null) {
                    texts.add("• Set with ${set.getWeightUI(2)}$weightLabel")
                } else {
                    val plural = if (set.reps == 1) "" else "s"
                    texts.add("• Set of ${set.reps} rep$plural")
                }

                i++
            }

            return texts
        }
    }
}