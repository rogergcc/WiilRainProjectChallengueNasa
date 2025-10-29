package com.rogergcc.wiilrainprojectchallenguenasa.domain.utils

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.WeatherResult


/**
 * Created on octubre.
 * year 2025 .
 */
fun calculateWeatherResult(
    yearlyData: List<Double>,
    weatherType: WeatherType,
): WeatherResult {
    val totalYears = yearlyData.size
    val eventYears = yearlyData.count { it > weatherType.extremeValue }
    val probability = (eventYears.toDouble() / totalYears) * 100.0
    val minValue = yearlyData.calculateMinOrZero()
    val maxValue = yearlyData.calculateMaxOrZero()
    val average = yearlyData.calculateAverageOrZero()

    val interpretation = buildInterpretation(
        threshold = weatherType.extremeValue,
        probability = probability,
        minValue = minValue,
        maxValue = maxValue,
        unit = weatherType.unit
    )

    return WeatherResult(
        average = average,
        probability = probability,
        eventYears = eventYears,
        totalYears = totalYears,
        minValue = minValue,
        maxValue = maxValue,
        interpretation = interpretation,
        weatherType = weatherType
    )
}

private fun buildInterpretation(
    threshold: Double,
    probability: Double,
    minValue: Double,
    maxValue: Double,
    unit: String,
): String = buildString {
    append("Probabilidad (>${threshold}${unit}): ${probability.formatOneDecimal()}%\n")
    append("Mínimo Histórico: ${minValue.formatOneDecimal()}${unit}\n")
    append("Máximo Histórico: ${maxValue.formatOneDecimal()}${unit}")
}