package com.rogergcc.wiilrainprojectchallenguenasa.data.dummy


/**
 * Created on octubre.
 * year 2025 .
 */
data class ForecastResponse(
    val location: Location,
    val event_date: String,
    val event_time: String,
    val forecast: Forecast,
    val hourly_breakdown: List<HourlyBreakdown>,
)

data class Location(
    val city: String,
    val latitude: Double,
    val longitude: Double,
)

data class Forecast(
    val will_rain: Boolean,
    val confidence: Int,
    val probability_of_precip: Int,
    val expected_intensity: String,
    val data_source: String,
    val next_update: String,
)

data class HourlyBreakdown(
    val time: String,
    val precip_prob: Int,
    val condition_icon: String,
)