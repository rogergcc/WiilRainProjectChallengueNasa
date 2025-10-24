package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details

import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.WeatherYearRecord
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.Thresholds
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DECIMAL_FORMAT_ONE
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TREND_THRESHOLD
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.formatTwoDecimals
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.providers.ResourceProvider
import kotlin.math.roundToInt


/**
 * Created on octubre.
 * year 2025 .
 */


////////////////////////////

class WeatherFormatter(private val resourceProvider: ResourceProvider) {
    private fun getTrendSummary(
        startYear: Int,
        endYear: Int,
        startValue: Double,
        endValue: Double,
        unit: String,
    ): String {
        val diff = endValue - startValue
        val arrow = when {
            diff > TREND_THRESHOLD -> "↗"
            diff < -TREND_THRESHOLD -> "↘"
            else -> "→"
        }
        val sign = if (diff > 0) "+" else ""
        return "Tendencia: $arrow $sign${DECIMAL_FORMAT_ONE.format(diff)}$unit ($startYear-$endYear)"
    }


    fun formatWeather(
        weatherType: WeatherType,
        yearlyData: List<WeatherYearRecord>,
        thresholds: Thresholds,
    ): String {


        return when (weatherType) {
            WeatherType.RAIN -> formatData(
                yearlyData,
                thresholds.rain,
                R.string.rain_historical,
                classification = { records ->
                    val rainCounts = RainRecommendation.entries.associateWith { range ->
                        records.count { range.matches(it.precip_mm.toFloat()) }
                    }
                    val total = records.size
                    rainCounts.entries.joinToString("\n") { (range, count) ->
                        val percentage = if (total > 0) (count * 100) / total else 0
                        "${range.emoji} ${range.message}: $count años (${percentage}%)"
                    }
                },
                recommendation = { records ->
                    val avg = records.map { it.precip_mm }.average().toFloat()
                    RainRecommendation.entries.firstOrNull { it.matches(avg) }?.let {
                        "${it.emoji} ${it.message}"
                    } ?: "No recommendation available."
                }
            )

            WeatherType.TEMP -> formatData(
                yearlyData,
                thresholds.temperature,
                R.string.temperature_historical,
                classification = { records ->
                    val tempCounts =
                        TemperatureRecommendation.entries.associateWith { recommendation ->
                            records.count { recommendation.condition(it.temp_c.toFloat()) }
                        }
                    val total = records.size
                    tempCounts.entries.joinToString("\n") { (recommendation, count) ->
                        val percentage = if (total > 0) (count * 100) / total else 0
                        "${recommendation.emoji} ${recommendation.message}: $count años (${percentage}%)"
                    }
                },
                recommendation = { records ->
                    val avg = records.map { it.temp_c }.average().toFloat()
                    TemperatureRecommendation.entries.firstOrNull { it.condition(avg) }?.let {
                        "${it.emoji} ${it.message}"
                    } ?: "No recommendation available."
                }
            )

            WeatherType.WIND -> formatData(
                yearlyData,
                thresholds.wind,
                R.string.wind_historical,
                classification = { records ->
                    val windCounts = WindRecommendation.entries.associateWith { range ->
                        records.count { range.condition(it.wind_kmh.toFloat()) }
                    }
                    val total = records.size
                    windCounts.entries.joinToString("\n") { (range, count) ->
                        val percentage = if (total > 0) (count * 100) / total else 0
                        "${range.emoji} ${range.message}: $count años (${percentage}%)"
                    }
                },
                recommendation = { records ->
                    val avg = records.map { it.wind_kmh }.average().toFloat()
                    WindRecommendation.entries.firstOrNull { it.condition(avg) }?.let {
                        "${it.emoji} ${it.message}"
                    } ?: "No recommendation available."
                }
            )
        }
    }


    private fun calculateStatistics(
        yearlyData: List<WeatherYearRecord>,
        thresholds: WeatherType
    ): Map<String, Any> {
        val max = yearlyData.maxOfOrNull { it.precip_mm } ?: 0.0
        val min = yearlyData.minOfOrNull { it.precip_mm } ?: 0.0
        val avg = yearlyData.map { it.precip_mm }.average()
        val heatYears = yearlyData.count { it.precip_mm > thresholds.extremeValue }

        return mapOf(
            "max" to max,
            "min" to min,
            "avg" to avg,
            "heatYears" to heatYears
        )
    }

    private fun generateVisualBarChart(
        yearlyData: List<WeatherYearRecord>,
        thresholds: WeatherType,
        max: Double
    ): String {
        return yearlyData.sortedByDescending { it.year }.joinToString("\n") { record ->
            val percent = ((record.precip_mm / max) * 100).roundToInt()
            val bar = "█".repeat(percent / 10) + "░".repeat(10 - percent / 10)
            val alert = if (record.precip_mm > thresholds.extremeValue) "⚠️" else ""
            "${record.year}: |$bar ${record.precip_mm.formatTwoDecimals()}${thresholds.unit} $alert"
        }
    }

    private fun formatData(
        yearlyData: List<WeatherYearRecord>,
        thresholds: WeatherType,
        titleResId: Int,
        classification: (List<WeatherYearRecord>) -> String,
        recommendation: (List<WeatherYearRecord>) -> String,
    ): String {
        val stats = calculateStatistics(yearlyData, thresholds)
        val max = stats["max"] as Double
        val min = stats["min"] as Double
        val avg = stats["avg"] as Double
        val heatYears = stats["heatYears"] as Int

        val trendSummary = getTrendSummary(
            yearlyData.first().year,
            yearlyData.last().year,
            yearlyData.first().precip_mm,
            yearlyData.last().precip_mm,
            thresholds.unit
        )
        val visualBarChart = generateVisualBarChart(yearlyData, thresholds, max)

        return """
${resourceProvider.getString(titleResId)}
$visualBarChart
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📈 ESTADÍSTICAS:
• Umbral ${thresholds.extremeValue}${thresholds.unit} (⚠️)
• Promedio: ${avg.formatTwoDecimals()} ${thresholds.unit}
• Máximo Histórico: ${max.formatTwoDecimals()} ${thresholds.unit}
• Mínimo Histórico: ${min.formatTwoDecimals()} ${thresholds.unit}
• $heatYears años con valores por encima del umbral

$trendSummary
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
+ CLASIFICACIÓN:
${classification(yearlyData)}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
💡 RECOMENDACIÓN:
${recommendation(yearlyData)}
""".trimIndent()
    }


}