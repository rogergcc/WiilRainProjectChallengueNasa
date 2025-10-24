package com.rogergcc.wiilrainprojectchallenguenasa.data.model


/**
 * Created on octubre.
 * year 2025 .
 */
data class Thresholds(
    val rain: WeatherType = WeatherType.RAIN,
    val temperature: WeatherType = WeatherType.TEMP,
    val wind: WeatherType = WeatherType.WIND
)


enum class WeatherType(
    val description: String,
    val extremeValue: Double,
    val unit: String
) {
    RAIN("rain", 1.0, "mm"),
    TEMP("temperature", 32.0, "°C"),
    WIND("wind", 20.0, "km/h");
    companion object {
        fun fromDescription(desc: String): WeatherType {
            return entries.first { it.description == desc }
        }
    }
}