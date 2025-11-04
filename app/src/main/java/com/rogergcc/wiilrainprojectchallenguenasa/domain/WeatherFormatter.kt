package com.rogergcc.wiilrainprojectchallenguenasa.domain

import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.WeatherYearRecord
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.domain.utils.formatTwoDecimalLocale
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DECIMAL_FORMAT_ONE
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TREND_THRESHOLD
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.formatTwoDecimals
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.providers.ResourceProvider
import kotlin.math.roundToInt


/**
 * Created on octubre.
 * year 2025 .
 */
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

        return resourceProvider.getString(
            R.string.label_trend,
            arrow,
            sign,
            DECIMAL_FORMAT_ONE.format(diff),
            unit,
            startYear,
            endYear
        )
    }
     fun formatWeather(
        weatherType: WeatherType,
        weatherYearRecords: List<WeatherYearRecord>,
    ): String {
        if (weatherYearRecords.isEmpty()) {
            return resourceProvider.getString(R.string.label_no_data, weatherType.name.lowercase())
        }

        val sortedRecords = weatherYearRecords.sortedBy { it.year }

        return when (weatherType) {
            WeatherType.RAIN -> buildWeatherReportSection(
                records = sortedRecords,
                weatherType = weatherType,
                titleResId = R.string.rain_historical,
                valueSelector = { it.precip_mm },
                recommendations = RainRecommendation.entries,
                condition = { rec, value -> rec.matches(value.toFloat()) }
            )

            WeatherType.TEMP -> buildWeatherReportSection(
                records = sortedRecords,
                weatherType = weatherType,
                titleResId = R.string.temperature_historical,
                valueSelector = { it.temp_c },
                recommendations = TemperatureRecommendation.entries,
                condition = { rec, value -> rec.matches(value.toFloat()) }
            )

            WeatherType.WIND -> buildWeatherReportSection(
                records = sortedRecords,
                weatherType = weatherType,
                titleResId = R.string.wind_historical,
                valueSelector = { it.wind_kmh },
                recommendations = WindRecommendation.entries,
                condition = { rec, value -> rec.matches(value.toFloat()) }
            )
        }
    }

    private fun buildRainClassification(
        records: List<WeatherYearRecord>,
        recommendations: Iterable<RainRecommendation>,
    ): String {
        val totalRecords = records.size
        val rainyRecords = records.count { it.precip_mm > 0 } // Contar registros con lluvia
        val rainProbability = if (totalRecords > 0) (rainyRecords * 100f) / totalRecords else 0f

        // Obtener la recomendación basada en la probabilidad de lluvia
        val recommendation = RainRecommendation.getRecommendation(rainProbability)

        // Construir el resultado
        return resourceProvider.getString(
            R.string.classification_child,
            recommendation.emoji,
            resourceProvider.getString(recommendation.labelRes),
            "(${recommendation.conditionRange.start} - ${recommendation.conditionRange.endInclusive})",
            rainyRecords,
            rainProbability.toInt()
        )
    }

    private fun <T> buildClassification(
        records: List<WeatherYearRecord>,
        recommendations: Iterable<T>,
        valueSelector: (WeatherYearRecord) -> Double,
        condition: (T, Double) -> Boolean,
    ): String where T : Enum<T>, T : Recommendation {
        val total = records.size
        return recommendations.joinToString("\n") { rec ->
            val count = records.count { condition(rec, valueSelector(it)) }
            val percentage = if (total > 0) (count * 100) / total else 0
            val range =
                when {
                    rec.conditionRange.start == -999f -> "(<= ${rec.conditionRange.endInclusive})"
                    rec.conditionRange.endInclusive == 999f -> "(>= ${rec.conditionRange.start})"
                    else -> "(${rec.conditionRange.start} - ${rec.conditionRange.endInclusive})"
                }
            resourceProvider.getString(
                R.string.classification_child,
                rec.emoji,
                resourceProvider.getString(rec.labelRes),
                range,
                count,
                percentage
            )
        }
    }


    // 🔹 Generic function to build weather report section
    private fun <T> buildWeatherReportSection(
        records: List<WeatherYearRecord>,
        weatherType: WeatherType,
        titleResId: Int,
        valueSelector: (WeatherYearRecord) -> Double,
        recommendations: Iterable<T>,
        condition: (T, Double) -> Boolean,
    ): String where T : Enum<T>, T : Recommendation {
        val weatherValueSelected = records.map(valueSelector)

        val statistics = calculateStatistics(weatherValueSelected, weatherType)

        if (weatherType == WeatherType.RAIN) {
            val classificationRain = buildRainClassification(records, RainRecommendation.entries)
            println("Rain classification:")
            println(classificationRain)
        }

        val trend = getTrendSummary(
            startYear = records.first().year,
            endYear = records.last().year,
            startValue = weatherValueSelected.first(),
            endValue = weatherValueSelected.last(),
            unit = weatherType.unit
        )
        val chart = generateVisualBarChart(records, weatherType, valueSelector, statistics.max)
        val classification = buildClassification(records, recommendations, valueSelector, condition)
        println("Generic classification:")
        println(classification)
        // todo Build recommendation based on average value + Generic for all weather types
        //WeatherType Temperature, Wind use avg and for Rain use probabability % already use in in Dashboard
//        val recommendation = buildRecommendation(statistics.avg, recommendations)

        return buildString {
            appendLine(resourceProvider.getString(titleResId))
            appendLine(chart)
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine(statistics.summary)
            appendLine(trend)
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine(resourceProvider.getString(R.string.label_classification))
            appendLine(resourceProvider.getString(weatherType.clasificationTitle))
            appendLine(classification)
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
//            appendLine(resourceProvider.getString(R.string.label_recommendation))
//            appendLine(recommendation)
        }.trimEnd()
    }

    private fun calculateStatistics(
        values: List<Double>,
        weatherType: WeatherType
    ): Statistics {

        val max = values.maxOrNull() ?: 0.0
        val min = values.minOrNull() ?: 0.0
        val avg = values.average()
        val extremeCount = values.count { it > weatherType.extremeValue }

        val summary = buildString {
            appendLine(resourceProvider.getString(R.string.label_statistics))
            appendLine(resourceProvider.getString(R.string.label_threshold, weatherType.extremeValue.toString(), weatherType.unit))
            appendLine(resourceProvider.getString(R.string.label_average, avg.formatTwoDecimals(), weatherType.unit))
            appendLine(resourceProvider.getString(R.string.label_maximum, max.formatTwoDecimalLocale(), weatherType.unit))
            appendLine(resourceProvider.getString(R.string.label_minimum, min.formatTwoDecimalLocale(), weatherType.unit))
            appendLine(resourceProvider.getString(R.string.label_above_threshold, extremeCount))
        }

        return Statistics(max =  max, min =  min, avg =  avg, summary =  summary)
    }
    private fun <T> buildRecommendation(
        avg: Double,
        recommendations: Iterable<T>,
    ): String where T : Enum<T>, T : Recommendation {
        val avgFloat = avg.toFloat()

        return recommendations.firstOrNull { it.matches(avgFloat) }
            ?.let {
                "${it.emoji} ${resourceProvider.getString(it.descRes)}"
            } ?: resourceProvider.getString(R.string.label_no_recommendation)
    }


    private fun generateVisualBarChart(
        records: List<WeatherYearRecord>,
        weatherType: WeatherType,
        valueSelector: (WeatherYearRecord) -> Double,
        max: Double,
    ): String {
        return records.sortedByDescending { it.year }.joinToString("\n") { record ->
            val value = valueSelector(record)
            val percent = if (max > 0) ((value / max) * 100).roundToInt() else 0
            val bar = "█".repeat(percent / 10).padEnd(10, '░')
            val alert = if (value > weatherType.extremeValue) "⚠️" else ""
            "${record.year}: |$bar ${value.formatTwoDecimals()}${weatherType.unit} $alert"
        }
    }
}


data class Statistics(
    val max: Double,
    val min: Double,
    val avg: Double,
    val summary: String
)