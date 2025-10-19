package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.Thresholds
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRecommendation
import kotlin.math.roundToInt


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
    val rain_event: Boolean,
    val heat_event: Boolean,
    val wind_event: Boolean,
)

enum class WeatherType(
    val description: String,
) {
    RAIN("rain"),
    TEMP("temperature"),
    WIND("wind");
    companion object {
        fun fromDescription(desc: String): WeatherType {
            return entries.first { it.description == desc }
        }
    }
}

object WeatherFormatter {

    /**
     * Calcula la tendencia entre dos valores (inicio y fin) y devuelve un texto formateado
     * con símbolo de tendencia (↗, ↘ o →) y diferencia numérica redondeada.
     *
     * @param startValue valor inicial del periodo
     * @param endValue valor final del periodo
     * @param startYear año inicial (opcional, solo para mostrar)
     * @param endYear año final (opcional, solo para mostrar)
     * @param unit unidad del valor, por ejemplo "°C", "mm", "km/h"
     */

    private fun getTrendSummary(
        startValue: Double,
        endValue: Double,
        startYear: Int,
        endYear: Int,
        unit: String
    ): String {
        val diff = endValue - startValue
        val arrow = when {
            diff > 0.1 -> "↗"
            diff < -0.1 -> "↘"
            else -> "→"
        }
        val sign = if (diff > 0) "+" else ""
        val formattedDiff = "%.1f".format(diff)
        return "Tendencia: $arrow $sign$formattedDiff$unit ($startYear-$endYear)"
    }

    fun formatTemperatureSection(startYear: Int, endYear: Int, startValue: Double, endValue: Double): String {
        val trend = getTrendSummary(startValue, endValue, startYear, endYear, "°C")
        return buildString {
            appendLine("│ ··· $trend │")
            appendLine("│ 📈 ESTADÍSTICAS: ...")
            appendLine("│ 💡 RECOMENDACIÓN: ...")
        }
    }

    fun formatWindSection(
        startYear: Int,
        endYear: Int,
        startValue: Double,
        endValue: Double,
        classification: String
    ): String {
        val trend = getTrendSummary(startValue, endValue, startYear, endYear, "km/h")
        return buildString {
            appendLine("│ ··· $trend │")
            appendLine("│ 📈 ESTADÍSTICAS: ...")
            appendLine(classification)
            appendLine("│ 💡 RECOMENDACIÓN: ...")
        }
    }

    // Generic formatter for weather data
    private fun formatData(
        type: String,
        weattherRecords: List<WeatherYearRecord>,
        thresholds: Thresholds,
        valueSelector: (WeatherYearRecord) -> Double,
        unit: String,
        alertCondition: (WeatherYearRecord) -> Boolean,
        title: String,
        thresholdValue: Double,
        classification: (List<WeatherYearRecord>)-> String ,
        recommendation: (List<WeatherYearRecord>) -> String,
    ): String {
        val max = (weattherRecords.maxOfOrNull(valueSelector) ?: 1.0).takeIf { it > 0 } ?: 1.0
        val min = weattherRecords.minOfOrNull(valueSelector) ?: 0.0
        val avg = weattherRecords.map(valueSelector).average()
//        val avgFormatted = "%.2f".format(avg)
        val valueListMape = weattherRecords.map { valueSelector(it) }
        val trendSummary = getTrendSummary(
            startValue = valueListMape.first(),
            endValue = valueListMape.last(),
            startYear = weattherRecords.first().year,
            endYear = weattherRecords.last().year,
            unit = unit
        )
        val lisSorted = weattherRecords.sortedByDescending { it.year }

        val heatYears = valueListMape.count { it > thresholdValue }
        val lines = lisSorted.joinToString("\n") { r ->
            val percent = ((valueSelector(r) / max) * 100).roundToInt()
            val barCount = (percent / 10).coerceAtMost(10)
            val bar = "█".repeat(barCount) + "░".repeat(10 - barCount)
            val alert = if (alertCondition(r)) "⚠️" else ""
            "%d: |%s %.1f%s %s".format(r.year, bar, valueSelector(r), unit, alert)
        }

        return """
$title
$lines
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📈 ESTADÍSTICAS:
• Umbral ${thresholdValue}${unit} (⚠️)
• Promedio: ${"%.2f".format(avg)} $unit
• Máximo Historico: ${"%.2f".format(max)} $unit
• Mínimo Historico: ${"%.2f".format(min)} $unit
• $heatYears años con valores por encima del umbral 

$trendSummary
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
+ CLASIFICACIÓN:
${classification(weattherRecords)} 
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
💡 RECOMENDACIÓN:
${recommendation(weattherRecords)}
""".trimIndent()
            }

    // 🌧️ Lluvia
    fun formatRain(data: List<WeatherYearRecord>, type:String, thresholds: Thresholds,): String {
        return formatData(
            type = type,
            weattherRecords = data,
            thresholds = thresholds,
            valueSelector = { it.precip_mm },
            unit = thresholds.unitPrecip,
            alertCondition = { it.rain_event || it.precip_mm > thresholds.rainLight },
            title = "🌧️ PRECIPITACIÓN HISTÓRICA",
            thresholdValue = thresholds.rainLight,
            classification = { records ->
                val windCounts = RainRecommendation.entries.associateWith { range ->
                    records.count { range.condition(it.precip_mm.toFloat()) }
                }
                val total = records.size
                val classificationData = windCounts.entries.joinToString("\n") { (range, count) ->
                    val percentage = if (total > 0) (count * 100) / total else 0
                    "${range.emoji} ${range.message}: $count años (${percentage}%)"
                }
                classificationData.trimIndent()
            }
            ,
            recommendation = { records ->
                val avg = records.map { it.precip_mm }.average().toFloat()
                val recommendation = RainRecommendation.entries.firstOrNull { it.condition(avg) }
                recommendation?.let {
                    "${it.emoji} ${it.message}"
                } ?: "No recommendation available."
//                when {
//                    records.any { it.precip_mm > thresholds.rainLight * 5 } -> "Lluvias intensas registradas. Precaución."
//                    records.any { it.precip_mm > thresholds.rainLight } -> "Precipitaciones leves en algunos años."
//                    else -> "Condiciones generalmente secas."
//                }
            }
        )
    }

    // 🌡️ Temperatura
    fun formatTemp(data: List<WeatherYearRecord>, type:String, thresholds: Thresholds): String {
        return formatData(
            type = type,
            weattherRecords = data,
            thresholds = thresholds,
            valueSelector = { it.temp_c },
            unit = thresholds.unitTemp,
            alertCondition = { it.heat_event || it.temp_c >= thresholds.hotExtreme },
            title = "🌡️ TEMPERATURA HISTÓRICA",
            thresholdValue = thresholds.hotExtreme,
            classification = { records ->
                val tempCounts = TemperatureRecommendation.entries.associateWith { recommendation ->
                    records.count { recommendation.condition(it.temp_c.toFloat()) }
                }
                val total = records.size
                val classificationData = tempCounts.entries.joinToString("\n") { (recommendation, count) ->
                    val percentage = if (total > 0) (count * 100) / total else 0
                    "${recommendation.emoji} ${recommendation.message}: $count años (${percentage}%)"
                }
                classificationData
            },

            recommendation = { records ->
                val avg = records.map { it.temp_c }.average().toFloat()
//                when {
//                    records.any { it.temp_c >= thresholds.hotExtreme } -> "Calor extremo en años recientes. Precaución en exteriores."
//                    avg > 30 -> "Clima cálido. Hidratación recomendada."
//                    avg < 25 -> "Temperaturas templadas, ideales para eventos al aire libre."
//                    else -> "Condiciones agradables y estables."
//                }
                val recommendation = TemperatureRecommendation.entries.firstOrNull { it.condition(avg) }
                recommendation?.let {
                    "${it.emoji} ${it.message}"
                } ?: "No recommendation available."
            }
        )
    }

    // 🌬️ Viento
    fun formatWind(data: List<WeatherYearRecord>, type:String, thresholds: Thresholds): String {
        return formatData(
            type = type,
            weattherRecords = data,
            thresholds = thresholds,
            valueSelector = { it.wind_kmh },
            unit = thresholds.unitWind,
            alertCondition = { it.wind_kmh >= thresholds.windStrong },
            title = "🌬️ VIENTO HISTÓRICO",
            thresholdValue = thresholds.windStrong,
            classification = { records ->
                val windCounts = WindRecommendation.entries.associateWith { range ->
                    records.count { range.condition(it.wind_kmh.toFloat()) }
                }
                val total = records.size
                val classificationData = windCounts.entries.joinToString("\n") { (range, count) ->
                    val percentage = if (total > 0) (count * 100) / total else 0
                    "${range.emoji} ${range.message}: $count años (${percentage}%)"
                }
                classificationData
            },

            recommendation = { records ->
//                val max = records.maxOfOrNull { it.wind_kmh } ?: 0.0

                val avg = records.map { it.wind_kmh }.average().toFloat()
//                when {
//                    max >= thresholds.windStrong -> "Vientos fuertes registrados. Precaución con estructuras ligeras."
//                    max >= thresholds.windStrong * 0.5 -> "Brisas moderadas comunes, sin riesgo notable."
//                    else -> "Condiciones calmadas. Ideales para eventos al aire libre."
//                }
                val recommendation = WindRecommendation.entries.firstOrNull { it.condition(avg) }
                recommendation?.let {
                    "${it.emoji} ${it.message}"
                } ?: "No recommendation available."

            }
        )
    }
}