package com.rogergcc.wiilrainprojectchallenguenasa.data

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.Thresholds
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.calculateAverageOrZero
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.calculateMaxOrZero
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.calculateMinOrZero
import com.rogergcc.wiilrainprojectchallenguenasa.domain.strategy.WeatherStrategyFactory
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
    val weatherType: WeatherType
)

data class ClimateAnalysisResult(
    val rain: WeatherResult,
    val temperature: WeatherResult,
    val wind: WeatherResult,
)

fun calculateWeatherAnalysis(
    yearlyData: List<YearlyData>,
): ClimateAnalysisResult {
    val mTresholds = Thresholds()

    val rainResult = WeatherStrategyFactory.getStrategy(
        weatherType = mTresholds.rain)
        .calculate(yearlyData, mTresholds.rain)

    val temperatureResult = WeatherStrategyFactory.getStrategy(
        weatherType = mTresholds.temperature)
        .calculate(yearlyData, mTresholds.temperature)

    val windResult = WeatherStrategyFactory.getStrategy(
        weatherType = mTresholds.wind)
        .calculate(yearlyData, mTresholds.wind)

    return ClimateAnalysisResult(
        rain = rainResult,
        temperature = temperatureResult,
        wind = windResult,
    )
}

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
): String {
    return buildString {
        append("Probabilidad (>${threshold}${unit}): ${probability.formatOneDecimal()}%\n")
        append("Mínimo Histórico: ${minValue.formatOneDecimal()}${unit}\n")
        append("Máximo Histórico: ${maxValue.formatOneDecimal()}${unit}")
    }
}