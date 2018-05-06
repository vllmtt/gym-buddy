package fi.anttonen.villematti.apps.gymbuddy.misc

import org.joda.time.Duration
import java.math.BigDecimal

fun Double.roundToDecimalPlaces(decimals: Int) =
        BigDecimal(this).setScale(decimals, BigDecimal.ROUND_HALF_UP).toDouble()
