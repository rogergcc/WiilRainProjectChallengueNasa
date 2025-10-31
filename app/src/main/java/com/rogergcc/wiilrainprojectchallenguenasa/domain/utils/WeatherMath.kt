package com.rogergcc.wiilrainprojectchallenguenasa.domain.utils

import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DECIMAL_FORMAT_ONE
import java.text.NumberFormat
import java.util.Locale


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
fun List<Double>.calculatePercentile(percentile: Double): Double {
    val sorted = this.sorted()
    val index = (percentile * (sorted.size - 1)).toInt()
    return sorted[index]
}


//fun Double.formatOneDecimal(): String = String.format(DECIMAL_FORMAT_ONE, this)

fun Double.formatOneDecimalLocale():String{
    val locale = Locale.getDefault()
    val formatter = NumberFormat.getNumberInstance(locale).apply { maximumFractionDigits = 1 }
    return formatter.format(this)
}

fun Double.formatTwoDecimalLocale():String{
    val locale = Locale.getDefault()
    val formatter = NumberFormat.getNumberInstance(locale).apply { maximumFractionDigits = 2 }
    return formatter.format(this)
}


fun Double.formatOneDecimal():String{
    return DECIMAL_FORMAT_ONE.format(this)
}
