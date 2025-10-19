package com.rogergcc.wiilrainprojectchallenguenasa.data.dummy

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData


/**
 * Created on octubre.
 * year 2025 .
 */

// Data classes para resultados (igual que antes)
data class RainResult(
    val probability: Double,
    val rainYears: Int,
    val totalYears: Int,
    val averageRain: Double,
    val averageRainIntensity: Double,
    val maxRain: Double,
    val heavyRainProbability: Double,
    val extremeRainProbability: Double,
    val visualBar: String,
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

private fun buildVisualBar(
    barLength: Int,
    probability: Double,
    rainRange: RainRange,
    useColoredBars: Boolean,
): String {
    val filledBlocks = (probability / 100.0 * barLength).toInt()
    val barContent = "█".repeat(filledBlocks) + "░".repeat(barLength - filledBlocks)

    return if (useColoredBars) {
        "${rainRange.emoji}$barContent"
    } else {
        barContent
    }
}

fun calculateRainProbabilityFromDataset(
    yearlyData: List<YearlyData>,
    rainThreshold: Double = 1.0,
    useColoredBars: Boolean = true,  // Nuevo parámetro para activar/desactivar colores
): RainResult {

    val BAR_LENGTH = 20
    val HEAVY_RAIN_THRESHOLD = 5.0
    val EXTREME_RAIN_THRESHOLD = 20.0
    val FREQUENT_RAIN_THRESHOLD = 60.0 // Para considerar "frecuente"

    val precipitations = yearlyData.map { it.precip_mm }
    val rainYears = yearlyData.count { it.precip_mm > rainThreshold }
    val totalYears = yearlyData.size
    val probability = (rainYears.toDouble() / totalYears) * 100.0
    val averageRain = precipitations.average()

    // Intesity on rainy days
    val rainyDaysData = precipitations.filter { it > rainThreshold }
    val maxRain = precipitations.maxOrNull() ?: 0.0
    val minRain = precipitations.minOrNull() ?: 0.0
    val averageRainIntensity = if (rainyDaysData.isNotEmpty()) rainyDaysData.average() else 0.0

    val heavyRainYears = yearlyData.count { it.precip_mm > HEAVY_RAIN_THRESHOLD }
    val heavyRainProbability = (heavyRainYears.toDouble() / totalYears) * 100.0

    val extremeRainYears = yearlyData.count { it.precip_mm > EXTREME_RAIN_THRESHOLD }
    val extremeRainProbability = (extremeRainYears.toDouble() / totalYears) * 100.0

    val rainRange = RainRange.fromValue(probability.toFloat())

    val visualBar = buildVisualBar(BAR_LENGTH, probability, rainRange, useColoredBars)

    val interpretation = "Probabilidad (${rainThreshold}mm) >: ${"%.1f".format(probability)}%" +
            "\n$rainYears años con lluvia" +
            "\nMinimo Historico ${"%.1f".format(minRain)} mm" +
            "\nMáximo histórico: ${"%.1f".format(maxRain)} mm"
//    val interpretation = buildString {
//            append("$rainYears de cada $totalYears años ha llovido")
//            append(" | ${rainRange.descripcion}")
//
//            if (rainYears > 0) {
//                append(" | Intensidad promedio: ${"%.1f".format(averageRainIntensity)} mm")
//            }
//
//            append(" | Máximo histórico: ${"%.1f".format(maxRain)} mm")
//
//            if (heavyRainProbability > 0) {
//                append(" | Lluvia intensa: ${"%.1f".format(heavyRainProbability)}%")
//            }
//
//    }

    return RainResult(
        interpretation = interpretation,
        probability = probability,
        rainYears = rainYears,
        totalYears = totalYears,
        averageRain = averageRain,
        averageRainIntensity = averageRainIntensity,
        maxRain = maxRain,
        heavyRainProbability = heavyRainProbability,
        extremeRainProbability = extremeRainProbability,
        visualBar = visualBar,
        rainRange = rainRange
    )
}

fun calculateTemperatureProbabilityFromDataset(
    yearlyData: List<YearlyData>,
    heatThreshold: Double = 32.0,
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
    val interpretation = "Probabilidad (>${heatThreshold}°C): ${"%.1f".format(heatProbability)}%" +
            "\nMinimo Historico ${"%.1f".format(minTemp)}°C" +
            "\nMáximo Historico ${"%.1f".format(maxTemp)}°C"

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
    strongWindThreshold: Double = 20.0,
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

    val interpretation =
        "Probabilidad (>${strongWindThreshold}km/h): ${"%.1f".format(strongWindProbability)}%" +
                "\nMinimo Historico ${"%.1f".format(minWind)} km/h" +
                "\nMáximo Historico ${"%.1f".format(maxWind)} km/h"

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