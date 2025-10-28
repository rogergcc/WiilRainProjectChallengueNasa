package com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges

import androidx.annotation.StringRes
import com.rogergcc.wiilrainprojectchallenguenasa.R


/**
 * Created on octubre.
 * year 2025 .
 */


enum class RainRange(
    val emoji: String,
    val description: String,
    val color: Int,
    val range: ClosedRange<Float>,
) {
    LOW("🟢", "Low Probability", R.color.verde, 0f..20f),        // 0% to 20% rain
    MEDIUM("🟡", "Medium Probability", R.color.amarillo, 20.1f..40f), // 20.1% to 40% rain
    HIGH("🟠", "High Probabiliy", R.color.naranja, 40.1f..60f), // 40.1% to 60% rain
    EXTREME("🔴", "Extreme Probability", R.color.rojo, 60.1f..100f);     // 60.1% to 100% rain

    companion object {
        fun fromValue(porcentaje: Float): RainRange {
            return entries.first { porcentaje in it.range }
        }

        // Método para obtener emoji
        fun getEmoji(porcentaje: Float): String {
            return fromValue(porcentaje).emoji
        }
    }
}

enum class RainRecommendation(
    override val emoji: String,
    @StringRes override val textRes: Int,
    private val conditionRange: ClosedRange<Float>
) : Recommendation {

    NO_RAIN("☁️", R.string.rain_no_expected, 0f..19.99f),
    LIGHT_RAIN("🌦️", R.string.rain_light, 20f..40f),
    MODERATE_RAIN("🌧️", R.string.rain_moderate, 40.1f..60f),
    HEAVY_RAIN("⛈️", R.string.rain_heavy, 60.1f..Float.MAX_VALUE);

    override fun matches(value: Float): Boolean = value in conditionRange

    companion object {
        fun getRecommendation(rainProb: Float): RainRecommendation =
            entries.first { it.matches(rainProb) }
    }
}