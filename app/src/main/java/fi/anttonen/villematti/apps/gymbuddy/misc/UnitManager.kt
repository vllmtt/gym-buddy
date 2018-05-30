package fi.anttonen.villematti.apps.gymbuddy.misc

object UnitManager {
    object Units {
        var weightRatio = WeightRatio.KG
        var distanceRatio = DistanceRatio.KM
    }

    object WeightRatio {
        const val KG = 1.0
        const val LBS = 2.20462
    }

    object DistanceRatio {
        const val KM = 1.0
        const val M = 3.28084 //0.621371

        const val METERS_IN_KM = 1000
        const val FEET_IN_M = 5280
    }
}