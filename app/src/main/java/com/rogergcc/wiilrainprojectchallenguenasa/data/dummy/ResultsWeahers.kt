package com.rogergcc.wiilrainprojectchallenguenasa.data.dummy



/**
 * Created on octubre.
 * year 2025 .
 */

data class WeatherYearRecord(
    val year: Int,
    val precip_mm: Double,
    val temp_c: Double,
    val wind_kmh: Double,
    val cloud_fraction: Double,
)


private fun calculateProbability(
    yearlyData: List<Double>,
    threshold: Double
): Pair<Double, Int> {
    val totalYears = yearlyData.size
    val eventYears = yearlyData.count { it > threshold }
    val probability = (eventYears.toDouble() / totalYears) * 100.0
    return Pair(probability, eventYears)
}

