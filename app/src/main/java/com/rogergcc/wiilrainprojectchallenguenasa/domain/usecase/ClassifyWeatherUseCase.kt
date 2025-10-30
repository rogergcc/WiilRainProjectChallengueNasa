package com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase

import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.WeatherYearRecord
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation


/**
 * Created on octubre.
 * year 2025 .
 */
class ClassifyWeatherUseCase {
    fun classify(
        records: List<WeatherYearRecord>,
        recommendations: Iterable<Recommendation>,
        valueSelector: (WeatherYearRecord) -> Double,
    ): List<ClassificationResult> {
        val total = records.size
        return recommendations.map { rec ->
            val count = records.count { rec.matches(valueSelector(it).toFloat()) }
            val percentage = if (total > 0) (count * 100) / total else 0
            ClassificationResult(rec, count, percentage)
        }
    }
}

data class ClassificationResult(
    val recommendation: Recommendation,
    val count: Int,
    val percentage: Int,
)

