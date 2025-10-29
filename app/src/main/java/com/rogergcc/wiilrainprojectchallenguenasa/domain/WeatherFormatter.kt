package com.rogergcc.wiilrainprojectchallenguenasa.domain

import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.WeatherYearRecord
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DECIMAL_FORMAT_ONE
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TREND_THRESHOLD
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.formatDecimalLocale
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.formatTwoDecimals
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.providers.ResourceProvider
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
            WeatherType.RAIN -> formatSection(
                records = sortedRecords,
                weatherType = weatherType,
                titleResId = R.string.rain_historical,
                valueSelector = { it.precip_mm },
                recommendations = RainRecommendation.entries,
                condition = { rec, value -> rec.matches(value.toFloat()) }
            )

            WeatherType.TEMP -> formatSection(
                records = sortedRecords,
                weatherType = weatherType,
                titleResId = R.string.temperature_historical,
                valueSelector = { it.temp_c },
                recommendations = TemperatureRecommendation.entries,
                condition = { rec, value -> rec.matches(value.toFloat()) }
            )

            WeatherType.WIND -> formatSection(
                records = sortedRecords,
                weatherType = weatherType,
                titleResId = R.string.wind_historical,
                valueSelector = { it.wind_kmh },
                recommendations = WindRecommendation.entries,
                condition = { rec, value -> rec.matches(value.toFloat()) }
            )
        }
    }


    // 🔹 Genérica para RAIN / TEMP / WIND
    private fun <T> formatSection(
        records: List<WeatherYearRecord>,
        weatherType: WeatherType,
        titleResId: Int,
        valueSelector: (WeatherYearRecord) -> Double,
        recommendations: Iterable<T>, // 👈 en lugar de Array<T>
        condition: (T, Double) -> Boolean,
    ): String where T : Enum<T>, T : Recommendation {

        val values = records.map(valueSelector)
        val max = values.maxOrNull() ?: 0.0
        val min = values.minOrNull() ?: 0.0
        val avg = values.average()
        val extremeCount = values.count { it > weatherType.extremeValue }

        val trend = getTrendSummary(
            startYear = records.first().year,
            endYear = records.last().year,
            startValue = values.first(),
            endValue = values.last(),
            unit = weatherType.unit
        )

        val chart = generateVisualBarChart(records, weatherType, valueSelector, max)

        val classification = buildClassification(records, recommendations, valueSelector, condition)
        val recommendation = buildRecommendation(avg, recommendations)

        return buildString {
            appendLine(resourceProvider.getString(titleResId))
            appendLine(chart)
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            appendLine(resourceProvider.getString(R.string.label_statistics))
            appendLine(resourceProvider.getString(R.string.label_threshold, weatherType.extremeValue.toString(), weatherType.unit))
            appendLine(resourceProvider.getString(R.string.label_average, avg.formatTwoDecimals(), weatherType.unit))
            appendLine(resourceProvider.getString(R.string.label_maximum, max.formatDecimalLocale(), weatherType.unit))
            appendLine(resourceProvider.getString(R.string.label_minimum, min.formatDecimalLocale(), weatherType.unit))
            appendLine(resourceProvider.getString(R.string.label_above_threshold, extremeCount))

            appendLine()
            appendLine(trend)
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            appendLine(resourceProvider.getString(R.string.label_classification))
            appendLine(classification)
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            appendLine(resourceProvider.getString(R.string.label_recommendation))
            appendLine(recommendation)
        }.trimEnd()
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
            "${rec.emoji} ${resourceProvider.getString(rec.textRes)}: $count años (${percentage}%)"
        }
    }

    private fun <T> buildRecommendation(
        avg: Double,
        recommendations: Iterable<T>,
    ): String where T : Enum<T>, T : Recommendation {
        val avgFloat = avg.toFloat()

        return recommendations.firstOrNull { it.matches(avgFloat) }?.let {
            "${it.emoji} ${resourceProvider.getString(it.textRes)}"
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