package com.rogergcc.wiilrainprojectchallenguenasa.data.dummy

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.Thresholds
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRange
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
) {
//    fun isEventExceeded(eventType: WeatherType): Boolean {
//        return when (eventType) {
//            WeatherType.RAIN -> thresholds.rain.exceeds(precip_mm)
//            WeatherType.TEMP -> thresholds.temperature.exceeds(temp_c)
//            WeatherType.WIND -> thresholds.wind.exceeds(wind_kmh)
//        }
//    }
}



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
    val visualScale: String,
    val interpretation: String,
)

data class WindResult(
    val averageWind: Double,
    val strongWindProbability: Double,
    val strongWindYears: Int,
    val totalYears: Int,
    val minWind: Double,
    val maxWind: Double,
    val visualScale: String,
    val interpretation: String,
)

data class ClimateAnalysis(
    val rain: RainResult,
    val temperature: TemperatureResult,
    val wind: WindResult,
    val metadata: Map<String, Any>,
)

//private fun buildVisualBar(
//    barLength: Int,
//    probability: Double,
//    range: Range, // Generalized to accept any range type
//    useColoredBars: Boolean
//): String {
//    val filledBlocks = (probability / 100.0 * barLength).toInt()
//    val barContent = "█".repeat(filledBlocks) + "░".repeat(barLength - filledBlocks)
//
//    return if (useColoredBars) {
//        "${range.emoji}$barContent"
//    } else {
//        barContent
//    }
//}
private fun calculateProbability(
    yearlyData: List<Double>,
    threshold: Double
): Pair<Double, Int> {
    val totalYears = yearlyData.size
    val eventYears = yearlyData.count { it > threshold }
    val probability = (eventYears.toDouble() / totalYears) * 100.0
    return Pair(probability, eventYears)
}

fun calculateRainProbabilityFromDataset(
    yearlyData: List<YearlyData>,
    rainThreshold: Double = Thresholds().rain.extremeValue,
    useColoredBars: Boolean = true
): RainResult {
    val precipitations = yearlyData.map { it.precip_mm }
    val (probability, rainYears) = calculateProbability(precipitations, rainThreshold)
    val averageRain = precipitations.average()
    val maxRain = precipitations.maxOrNull() ?: 0.0
    val minRain = precipitations.minOrNull() ?: 0.0

    val rainRange = RainRange.fromValue(probability.toFloat())
//    val visualBar = buildVisualBar(20, probability, rainRange, useColoredBars)


    val interpretation = buildInterpretation(
        threshold = rainThreshold,
        probability = probability,
        minValue = minRain,
        maxValue = maxRain,
        unit = "mm"
    )
    return RainResult(
        interpretation = interpretation,
        probability = probability,
        rainYears = rainYears,
        totalYears = yearlyData.size,
        averageRain = averageRain,
        averageRainIntensity = precipitations.filter { it > rainThreshold }.average(),
        maxRain = maxRain,
        heavyRainProbability = calculateProbability(precipitations, 5.0).first,
        extremeRainProbability = calculateProbability(precipitations, 20.0).first,
//        visualBar = visualBar,
        rainRange = rainRange
    )
}

fun calculateTemperatureProbabilityFromDataset(
    yearlyData: List<YearlyData>,
    heatThreshold: Double = Thresholds().temperature.extremeValue,
): TemperatureResult {

    val SCALE_LENGTH = 20        // Número de posiciones en la escala visual
    val DEFAULT_NORMALIZED_POSITION = 0.5  // Posición central cuando no hay rango
    val SCALE_MULTIPLIER = 19     // Para convertir 0-1 a 0-9 (SCALE_LENGTH - 1)


    val temperatures = yearlyData.map { it.temp_c }
    val averageTemp = temperatures.average()
    val heatYears = yearlyData.count { it.temp_c > heatThreshold }
    val totalYears = yearlyData.size
    val heatProbability = (heatYears.toDouble() / totalYears) * 100.0

    val minTemp = temperatures.minOrNull() ?: 0.0
    val maxTemp = temperatures.maxOrNull() ?: 0.0

    val sortedTemps = temperatures.sorted()

    val percentile25 = sortedTemps[sortedTemps.size / 4]
    val percentile75 = sortedTemps[3 * sortedTemps.size / 4]

    val scaleMin = percentile25  // Usar percentil 25 como mínimo de escala
    val scaleMax = percentile75  // Usar percentil 75 como máximo de escala

    val effectiveRange = scaleMax - scaleMin
    val normalizedPosition = if (effectiveRange == 0.0) {
        DEFAULT_NORMALIZED_POSITION
    } else {
        // Sure position between 0 and 1
        ((averageTemp - scaleMin) / effectiveRange).coerceIn(0.0, 1.0)
    }

    val position = (normalizedPosition * SCALE_MULTIPLIER).toInt()

    // Visual demo scale
    val scale = MutableList(SCALE_LENGTH) { "─" }
    scale[position] = "┼"
    val visualScale = "🥶${scale.joinToString("")}🥵"

//    val interpretation = "Probabilidad >${heatThreshold}°C: ${"%.1f".format(heatProbability)}%"
//    val interpretation = buildString {
//        append("•🌡️ Temperatura promedio histórica: ${"%.1f".format(averageTemp)}°C \n")
//        append("•📊 Rango histórico completo: ${"%.1f".format(minTemp)}°C - ${"%.1f".format(maxTemp)}°C \n")
//        append("•⚠️ Probabilidad de calor extremo (>$heatThreshold°C): ${"%.1f".format(heatProbability)}% (BAJO)")
//    }


    val interpretation = buildInterpretation(
        threshold = heatThreshold,
        probability = heatProbability,
        minValue = minTemp,
        maxValue = maxTemp,
        unit = "°C"
    )
    return TemperatureResult(
        averageTemperature = averageTemp,
        heatProbability = heatProbability,
        heatYears = heatYears,
        totalYears = totalYears,
        minTemperature = minTemp,
        maxTemperature = maxTemp,
        visualScale = visualScale,
        interpretation = interpretation,
    )
}

fun calculateWindProbabilityFromDataset(
    yearlyData: List<YearlyData>,
    strongWindThreshold: Double = Thresholds().wind.extremeValue,
): WindResult {
    val SCALE_LENGTH = 20
    val SCALE_MULTIPLIER = 19

    val LIGHT_WIND_THRESHOLD = 10.0
    val MODERATE_WIND_THRESHOLD = 15.0
    val STRONG_WIND_THRESHOLD = 30.0
    val VERY_STRONG_WIND_THRESHOLD = 50.0

    val MAX_SCALE_ADJUSTMENT = SCALE_MULTIPLIER - 1

    val windSpeeds = yearlyData.map { it.wind_kmh }
    val averageWind = windSpeeds.average()
    val strongWindYears = yearlyData.count { it.wind_kmh > strongWindThreshold }
    val totalYears = yearlyData.size
    val strongWindProbability = (strongWindYears.toDouble() / totalYears) * 100.0

    val minWind = windSpeeds.minOrNull() ?: 0.0
    val maxWind = windSpeeds.maxOrNull() ?: 0.0

    // optimal scale for extreme values
    val sortedWinds = windSpeeds.sorted()
    val percentile90 = sortedWinds[9 * sortedWinds.size / 10]

    // Use the maximum between the 90th percentile and a reasonable minimum
    val scaleMax = maxOf(strongWindThreshold * 2, percentile90, 30.0)

    val normalizedPosition = (averageWind / scaleMax).coerceIn(0.0, 1.0)
    val position = (normalizedPosition * SCALE_MULTIPLIER).toInt()


    val windRange = WindRange.fromRange(averageWind.toFloat())

    val scale = MutableList(SCALE_LENGTH) { "─" }
    scale[minOf(position, MAX_SCALE_ADJUSTMENT)] = windRange.emoji
    val visualScale = "${WindRange.CALM.emoji} ${scale.joinToString("")} ${WindRange.EXTREME.emoji}"

//    val interpretation = buildString {
//        append("Velocidad promedio: ${"%.1f".format(averageWind)} km/h")
//        append(" | $intensityDescription")
//        append(" | Prob. >${strongWindThreshold}km/h: ${"%.1f".format(strongWindProbability)}%")
//        append(" | Máximo: ${"%.1f".format(maxWind)} km/h")
//    }


    val interpretation = buildInterpretation(
        threshold = strongWindThreshold,
        probability = strongWindProbability,
        minValue = minWind,
        maxValue = maxWind,
        unit = "km/h"
    )

    return WindResult(
        averageWind = averageWind,
        strongWindProbability = strongWindProbability,
        strongWindYears = strongWindYears,
        totalYears = totalYears,
        minWind = minWind,
        maxWind = maxWind,
        visualScale = visualScale,
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