/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.misc

import org.joda.time.Duration
import java.math.BigDecimal

fun Double.roundToDecimalPlaces(decimals: Int) =
        BigDecimal(this).setScale(decimals, BigDecimal.ROUND_HALF_UP).toDouble()

fun Long.format(digits: Int) = java.lang.String.format("%0${digits}d", this)