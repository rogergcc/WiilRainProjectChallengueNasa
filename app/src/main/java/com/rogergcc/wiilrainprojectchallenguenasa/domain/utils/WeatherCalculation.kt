package com.rogergcc.wiilrainprojectchallenguenasa.domain.utils

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.WeatherResult


/**
 * Created on octubre.
 * year 2025 .
 */


fun calculateWeatherResult(
    yearlyData: List<Double>,
    weatherType: WeatherType,
    recommendationProvider: (Double) -> Recommendation,

    ): WeatherResult {
    val totalYears = yearlyData.size
    val eventYears = yearlyData.count { it > weatherType.extremeValue }
    val probability = (eventYears.toDouble() / totalYears) * 100.0
    val minValue = yearlyData.calculateMinOrZero()
    val maxValue = yearlyData.calculateMaxOrZero()
    val average = yearlyData.calculateAverageOrZero()


    // Obtain recommendation based on average value generic form for each weather type Enum
    val recommendation: Recommendation = recommendationProvider(average)

    return WeatherResult(
        average = average,
        probability = probability,
        eventYears = eventYears,
        totalYears = totalYears,
        minValue = minValue,
        maxValue = maxValue,
//        interpretation = interpretation,
        weatherType = weatherType,
        recomendation = recommendation
    )
}

fun <T> buildRecommendation(
    recommendations: Iterable<T>,
    valueSelector: Double,
    condition: (T, Double) -> Boolean,
): T? where T : Enum<T>, T : Recommendation {
    val matchedRecommendation = recommendations.find { rec ->
        condition(rec, valueSelector)
    }
    return matchedRecommendation
}


private fun buildInterpretation(
    threshold: Double,
    probability: Double,
    minValue: Double,
    maxValue: Double,
    unit: String,
): String = buildString {
    append("Probabilidad (>${threshold}${unit}): ${probability.formatTwoDecimalLocale()}%\n")
    append("Mínimo Histórico: ${minValue.formatTwoDecimalLocale()}${unit}\n")
    append("Máximo Histórico: ${maxValue.formatTwoDecimalLocale()}${unit}")
}