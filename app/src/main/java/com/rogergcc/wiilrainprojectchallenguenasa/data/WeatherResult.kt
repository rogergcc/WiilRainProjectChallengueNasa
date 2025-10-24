package com.rogergcc.wiilrainprojectchallenguenasa.data

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.Thresholds
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherStrategyFactory
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.formatOneDecimal


/**
 * Created on octubre.
 * year 2025 .
 */
data class WeatherResult(
    val average: Double,
    val probability: Double,
    val eventYears: Int,
    val totalYears: Int,
    val minValue: Double,
    val maxValue: Double,
    val interpretation: String,
)
data class ClimateAnalysisResult(
    val rain: WeatherResult,
    val temperature: WeatherResult,
    val wind: WeatherResult,
)
fun calculateWeatherAnalysis(
    yearlyData: List<YearlyData>,
    thresholds: Thresholds
): ClimateAnalysisResult {
    val rainResult = WeatherStrategyFactory.getStrategy(thresholds.rain)
        .calculate(yearlyData, thresholds.rain)

    val temperatureResult = WeatherStrategyFactory.getStrategy(thresholds.temperature)
        .calculate(yearlyData, thresholds.temperature)

    val windResult = WeatherStrategyFactory.getStrategy(thresholds.wind)
        .calculate(yearlyData, thresholds.wind)

    return ClimateAnalysisResult(
        rain = rainResult,
        temperature = temperatureResult,
        wind = windResult,
    )
}

fun calculateWeatherResult(
    yearlyData: List<Double>,
    threshold: Double,
    unit: String,
): WeatherResult {
    val totalYears = yearlyData.size
    val eventYears = yearlyData.count { it > threshold }
    val probability = (eventYears.toDouble() / totalYears) * 100.0
    val minValue = yearlyData.calculateMinOrZero()
    val maxValue = yearlyData.calculateMaxOrZero()
    val average = yearlyData.calculateAverageOrZero()

    val interpretation = buildInterpretation(
        threshold = threshold,
        probability = probability,
        minValue = minValue,
        maxValue = maxValue,
        unit = unit
    )

    return WeatherResult(
        average = average,
        probability = probability,
        eventYears = eventYears,
        totalYears = totalYears,
        minValue = minValue,
        maxValue = maxValue,
        interpretation = interpretation
    )
}

private fun List<Double>.calculateMinOrZero() = this.minOrNull() ?: 0.0
private fun List<Double>.calculateMaxOrZero() = this.maxOrNull() ?: 0.0
private fun List<Double>.calculateAverageOrZero() = if (this.isNotEmpty()) this.average() else 0.0
private fun List<Double>.calculatePercentile(percentile: Double): Double {
    val sorted = this.sorted()
    val index = (percentile * (sorted.size - 1)).toInt()
    return sorted[index]
}

private fun buildInterpretation(
    threshold: Double,
    probability: Double,
    minValue: Double,
    maxValue: Double,
    unit: String,
): String {
    return buildString {
        append("Probabilidad (>${threshold}${unit}): ${probability.formatOneDecimal()}%\n")
        append("Mínimo Histórico: ${minValue.formatOneDecimal()}${unit}\n")
        append("Máximo Histórico: ${maxValue.formatOneDecimal()}${unit}")
    }
}