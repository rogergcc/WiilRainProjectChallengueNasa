package com.rogergcc.wiilrainprojectchallenguenasa.domain.utils

import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DECIMAL_FORMAT_ONE


/**
 * Created on octubre.
 * year 2025 .
 */
fun List<Double>.calculateAverageOrZero(): Double =
    if (isNotEmpty()) sum() / size else 0.0

fun List<Double>.calculateMinOrZero(): Double =
    if (isNotEmpty()) minOrNull() ?: 0.0 else 0.0

fun List<Double>.calculateMaxOrZero(): Double =
    if (isNotEmpty()) maxOrNull() ?: 0.0 else 0.0

fun Double.formatOneDecimal(): String = String.format(DECIMAL_FORMAT_ONE, this)