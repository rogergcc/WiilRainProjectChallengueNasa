package com.rogergcc.wiilrainprojectchallenguenasa.data.model


/**
 * Created on octubre.
 * year 2025 .
 */


fun List<Double>.calculateMinOrZero() = this.minOrNull() ?: 0.0
fun List<Double>.calculateMaxOrZero() = this.maxOrNull() ?: 0.0
fun List<Double>.calculateAverageOrZero() =
    if (this.isNotEmpty()) this.average() else 0.0

private fun List<Double>.calculatePercentile(percentile: Double): Double {
    val sorted = this.sorted()
    val index = (percentile * (sorted.size - 1)).toInt()
    return sorted[index]
}
