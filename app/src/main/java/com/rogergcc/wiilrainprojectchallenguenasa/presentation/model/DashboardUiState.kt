package com.rogergcc.wiilrainprojectchallenguenasa.presentation.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation


/**
 * Created on noviembre.
 * year 2025 .
 */
data class DashboardUiState(
    val cityCountry: String = "",
    val countObservations: String = "",
    val periodYearsObservation: String = "",
    val dateSearch: String = "",

    val rain: WeatherResult,
    val temperature: WeatherResult,
    val wind: WeatherResult,

//    val rainAverage: Float = 0f,
//    val rainProbability: Float = 0f,
//    val rainProbabilityPercentage: String = "",
//    val rainEmoji: String = "",
//    @StringRes val rainLabelRes: Int = 0,
//    @StringRes val rainDescRes: Int = 0,
//    @ColorRes val rainColorRes: Int = 0,
//
//    val tempAverage: Float = 0f,
//    val tempProbability: Float = 0f,
//    val tempProbabilityPercentage: String = "",
//    val tempEmoji: String = "",
//    @StringRes val tempLabelRes: Int = 0,
//    @StringRes val tempDescRes: Int = 0,
//    @ColorRes val tempColorRes: Int = 0,
//
//    val windAverage: Float = 0f,
//    val windProbability: Float = 0f,
//    val windProbabilityPercentage: String = "",
//    val windEmoji: String = "",
//    @StringRes val windLabelRes: Int = 0,
//    @StringRes val windDescRes: Int = 0,
//    @ColorRes val windColorRes: Int = 0,
)


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