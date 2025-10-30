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
    override val emoji: String,
    @StringRes override val textRes: Int,
    private val condition: (Float) -> Boolean
) : Recommendation {

    LIGHT_BREEZE("🍃", R.string.wind_light, { wind -> wind < 10 }),
    MODERATE_BREEZE("🎐", R.string.wind_moderate, { wind -> wind in 10f..20f }),
    STRONG_WINDS("🌪", R.string.wind_strong, { wind -> wind > 20 });

    override fun matches(value: Float): Boolean = condition(value)

    companion object {
        fun getRecommendation(rainProb: Float): WindRecommendation =
            WindRecommendation.entries.first { it.matches(rainProb) }
    }
}