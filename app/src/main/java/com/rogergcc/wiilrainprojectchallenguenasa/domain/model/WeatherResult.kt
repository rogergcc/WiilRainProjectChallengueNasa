package com.rogergcc.wiilrainprojectchallenguenasa.domain.model

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType


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
    val weatherType: WeatherType,
)

data class ClimateAnalysisResult(
    val rain: WeatherResult,
    val temperature: WeatherResult,
    val wind: WeatherResult,
)