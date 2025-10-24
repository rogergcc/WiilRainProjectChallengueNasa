package com.rogergcc.wiilrainprojectchallenguenasa.data.dummy

import com.rogergcc.wiilrainprojectchallenguenasa.data.WeatherResult
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.Thresholds
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.formatOneDecimal


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
    val thresholds: Thresholds
)



data class RainResult(
    val probability: Double,
    val rainYears: Int,
    val totalYears: Int,
    val averageRain: Double,
    val averageRainIntensity: Double,
    val maxRain: Double,
    val heavyRainProbability: Double,
    val extremeRainProbability: Double,
    val visualBar: String = "",
    val interpretation: String,
    val rainRange: RainRange,  // ← USA TU ENUM EXISTENTE
)

data class TemperatureResult(
    val averageTemperature: Double,
    val heatProbability: Double,
    val heatYears: Int,
    val totalYears: Int,
    val minTemperature: Double,
    val maxTemperature: Double,
    val interpretation: String,
)

data class WindResult(
    val averageWind: Double,
    val strongWindProbability: Double,
    val strongWindYears: Int,
    val totalYears: Int,
    val minWind: Double,
    val maxWind: Double,
    val interpretation: String,
)

data class ClimateAnalysis(
    val rain: RainResult,
    val temperature: TemperatureResult,
    val wind: WindResult,
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


private fun List<Double>.calculateMinOrZero() = this.minOrNull() ?: 0.0
private fun List<Double>.calculateMaxOrZero() = this.maxOrNull() ?: 0.0
private fun List<Double>.calculateAverageOrZero() = if (this.isNotEmpty()) this.average() else 0.0
private fun List<Double>.calculatePercentile(percentile: Double): Double {
    val sorted = this.sorted()
    val index = (percentile * (sorted.size - 1)).toInt()
    return sorted[index]
}

fun calculateRainProbabilityFromDataset(
    yearlyData: List<YearlyData>,
    weatherType: WeatherType = Thresholds().rain
): RainResult {
    val precipitations = yearlyData.map { it.precip_mm }
    val (probability, rainYears) = calculateProbability(precipitations, weatherType.extremeValue)
    val minRain = precipitations.calculateMinOrZero()
    val maxRain = precipitations.calculateMaxOrZero()
    val averageRain = precipitations.calculateAverageOrZero()

    val rainRange = RainRange.fromValue(probability.toFloat())


    val interpretation = buildInterpretation(
        threshold = weatherType.extremeValue,
        probability = probability,
        minValue = minRain,
        maxValue = maxRain,
        unit = weatherType.unit
    )
    return RainResult(
        interpretation = interpretation,
        probability = probability,
        rainYears = rainYears,
        totalYears = yearlyData.size,
        averageRain = averageRain,
        averageRainIntensity = precipitations.filter { it > weatherType.extremeValue }.average(),
        maxRain = maxRain,
        heavyRainProbability = calculateProbability(precipitations, 5.0).first,
        extremeRainProbability = calculateProbability(precipitations, 20.0).first,
        rainRange = rainRange
    )
}

fun calculateTemperatureProbabilityFromDataset(
    yearlyData: List<YearlyData>,
    weatherType: WeatherType = Thresholds().temperature,
): TemperatureResult {

    val temperatures = yearlyData.map { it.temp_c }
    val heatYears = yearlyData.count { it.temp_c > weatherType.extremeValue }
    val totalYears = yearlyData.size
    val heatProbability = (heatYears.toDouble() / totalYears) * 100.0

    val minTemp = temperatures.calculateMinOrZero()
    val maxTemp = temperatures.calculateMaxOrZero()
    val averageTemp = temperatures.calculateAverageOrZero()

    val sortedTemps = temperatures.sorted()

    val percentile25 = sortedTemps.calculatePercentile(0.25)
    val percentile75 = sortedTemps.calculatePercentile(0.75)


    val scaleMin = percentile25  // Usar percentil 25 como mínimo de escala
    val scaleMax = percentile75  // Usar percentil 75 como máximo de escala



    val interpretation = buildInterpretation(
        threshold = weatherType.extremeValue,
        probability = heatProbability,
        minValue = minTemp,
        maxValue = maxTemp,
        unit = weatherType.unit
    )
    return TemperatureResult(
        averageTemperature = averageTemp,
        heatProbability = heatProbability,
        heatYears = heatYears,
        totalYears = totalYears,
        minTemperature = minTemp,
        maxTemperature = maxTemp,
        interpretation = interpretation,
    )
}

fun calculateWindProbabilityFromDataset(
    yearlyData: List<YearlyData>,
    weatherType: WeatherType = Thresholds().wind,
): WindResult {

    val windSpeeds = yearlyData.map { it.wind_kmh }
    val strongWindYears = yearlyData.count { it.wind_kmh > weatherType.extremeValue }
    val totalYears = yearlyData.size
    val strongWindProbability = (strongWindYears.toDouble() / totalYears) * 100.0

    val minWind = windSpeeds.calculateMinOrZero()
    val maxWind = windSpeeds.calculateMaxOrZero()
    val averageWind = windSpeeds.calculateAverageOrZero()

    val interpretation = buildInterpretation(
        threshold = weatherType.extremeValue,
        probability = strongWindProbability,
        minValue = minWind,
        maxValue = maxWind,
        unit = weatherType.unit
    )

    return WindResult(
        averageWind = averageWind,
        strongWindProbability = strongWindProbability,
        strongWindYears = strongWindYears,
        totalYears = totalYears,
        minWind = minWind,
        maxWind = maxWind,
        interpretation = interpretation
    )
}

private fun buildInterpretation(
    threshold: Double,
    probability: Double,
    minValue: Double,
    maxValue: Double,
    unit: String
): String {
    return buildString {
        append("Probabilidad (>${threshold}${unit}): ${probability.formatOneDecimal()}%\n")
        append("Mínimo Histórico: ${minValue.formatOneDecimal()}${unit}\n")
        append("Máximo Histórico: ${maxValue.formatOneDecimal()}${unit}")
    }
}