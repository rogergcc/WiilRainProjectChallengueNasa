package com.rogergcc.wiilrainprojectchallenguenasa.data.model

import android.content.Context
import com.rogergcc.wiilrainprojectchallenguenasa.R


/**
 * Created on octubre.
 * year 2025 .
 */

enum class WeatherType(
    val description: Int ,
    val extremeValue: Double,
    val unit: String,
    val clasificationTitle: Int
) {
    RAIN(R.string.description_rain, 1.0, "mm", R.string.rain_clasification_title),
    TEMP(R.string.description_temperature, 32.0, "°C", R.string.temperature_clasification_title),
    WIND(R.string.description_wind, 20.0, "km/h", R.string.wind_clasification_title);
    companion object {
        fun fromDescription(desc: String, context: Context): WeatherType {
            return entries.first { context.getString(it.description) == desc }
        }
    }
}