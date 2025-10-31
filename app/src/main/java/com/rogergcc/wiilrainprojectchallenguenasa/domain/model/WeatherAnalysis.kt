package com.rogergcc.wiilrainprojectchallenguenasa.domain.model


/**
 * Created on octubre.
 * year 2025 .
 */
data class WeatherAnalysis(
    val avg: Double,
    val max: Double,
    val min: Double,
    val trend: Trend,
    val startYear: Int,
    val endYear: Int,
    val countExtreme: Int
) {
    companion object {
        val Empty = WeatherAnalysis(
            avg = 0.0,
            max = 0.0,
            min = 0.0,
            trend = Trend.STABLE,
            startYear = 0,
            endYear = 0,
            countExtreme = 0
        )
    }
}

data class Trend(val name: String) {
    companion object {
        val UP = Trend("UP")
        val DOWN = Trend("DOWN")
        val STABLE = Trend("STABLE")
    }
}
