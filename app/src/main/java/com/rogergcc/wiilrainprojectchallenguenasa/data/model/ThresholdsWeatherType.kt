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
    val unit: String
) {
    RAIN(R.string.description_rain, 1.0, "mm"),
    TEMP(R.string.description_temperature, 32.0, "°C"),
    WIND(R.string.description_wind, 20.0, "km/h");
    companion object {
        fun fromDescription(desc: String, context: Context): WeatherType {
            return entries.first { context.getString(it.description) == desc }
        }
    }
}