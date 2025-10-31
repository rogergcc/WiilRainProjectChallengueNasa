package com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges

import androidx.annotation.StringRes
import com.rogergcc.wiilrainprojectchallenguenasa.R


/**
 * Created on octubre.
 * year 2025 .
 */

enum class WindRange(
    val emoji: String,
    val description: String,
    val color: Int,
    val range: ClosedRange<Float>,
) {
    CALM("\uD83C\uDF43", "Light wind", R.color.celeste, 0f..10f),                     // Light blue
    MODERATE("\uD83C\uDF90", "Moderate wind", R.color.light_blue, 11.0f..15.0f),        // Sky blue
    STRONG("\uD83C\uDF2C\uFE0F", "Stronge wind", R.color.azul, 16.0f..30.0f),                 // Medium blue
    VERY_STRONG("\uD83D\uDCA8", "very strong wind", R.color.blue_dark, 31f..50.0f),// Dark blue
    EXTREME("\uD83C\uDF2A\uFE0F", "Extreme wind", R.color.purple, 51.0f..Float.MAX_VALUE); // Purple

    companion object {
        fun fromRange(speed: Float): WindRange {
            return entries.first { speed in it.range }
        }
    }
}

enum class WindRecommendation(
    private val conditionRange: ClosedRange<Float>,
    override val emoji: String,
    override val color: Int, // Added color field
    @StringRes override val labelRes: Int,
    @StringRes override val descRes: Int
) : Recommendation {

//    LIGHT_BREEZE("🍃", R.string.wind_light, R.color.light_green, { wind -> wind < 10 }),
//    MODERATE_BREEZE("🎐", R.string.wind_moderate, R.color.yellow, { wind -> wind in 10f..20f }),
//    STRONG_WINDS("🌪", R.string.wind_strong, R.color.orange, { wind -> wind > 20 });

    WIND_CALM(0f..5f, "🌿", R.color.wind_calm, R.string.wind_calm_label, R.string.wind_calm_desc),
    WIND_LIGHT(6f.. 15f, "🍃", R.color.wind_light, R.string.wind_light_label, R.string.wind_light_desc),
    WIND_MODERATE(16f.. 30f, "🌬️", R.color.wind_moderate, R.string.wind_moderate_label, R.string.wind_moderate_desc),
    WIND_STRONG(31f.. 50f, "💨", R.color.wind_strong, R.string.wind_strong_label, R.string.wind_strong_desc),
    WIND_VERY_STRONG(51f.. 999f, "🌪️", R.color.wind_very_strong, R.string.wind_very_strong_label, R.string.wind_very_strong_desc);


    override fun matches(value: Float): Boolean = value in conditionRange

    companion object {
        fun getRecommendation(windSpeed: Float): WindRecommendation =
            entries.first { it.matches(windSpeed) }
    }
}