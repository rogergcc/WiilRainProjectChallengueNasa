package com.rogergcc.wiilrainprojectchallenguenasa.domain.model

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation


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
    val weatherType: WeatherType,
    var recomendation: Recommendation
    )

data class ClimateAnalysisResult(
    val rain: WeatherResult,
    val temperature: WeatherResult,
    val wind: WeatherResult,
)