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
    private val conditionRange: ClosedRange<Float>,
    override val emoji: String,
    override val color: Int, // Added color field
    @StringRes override val labelRes: Int,
    @StringRes override val descRes: Int
) : Recommendation {

//    NO_RAIN("☁️", R.string.rain_no_expected, R.color.light_gray, 0f..19.99f),
//    LIGHT_RAIN("🌦️", R.string.rain_light, R.color.light_blue, 20f..40f),
//    MODERATE_RAIN("🌧️", R.string.rain_moderate, R.color.blue, 40.1f..60f),
//    HEAVY_RAIN("⛈️", R.string.rain_heavy, R.color.dark_blue, 60.1f..Float.MAX_VALUE);

    RAIN_VERY_LOW(0f.. 10f, "☁️", R.color.rain_very_low, R.string.rain_very_low_label, R.string.rain_very_low_desc),
    RAIN_LOW(11f.. 30f, "🌤️", R.color.rain_low, R.string.rain_low_label, R.string.rain_low_desc),
    RAIN_MODERATE(31f.. 60f, "🌧️", R.color.rain_moderate, R.string.rain_moderate_label, R.string.rain_moderate_desc),
    RAIN_HIGH(61f.. 80f, "🌦️", R.color.rain_high, R.string.rain_high_label, R.string.rain_high_desc),
    RAIN_VERY_HIGH(81f.. 100f, "⛈️", R.color.rain_very_high, R.string.rain_very_high_label, R.string.rain_very_high_desc);

    override fun matches(value: Float): Boolean = value in conditionRange

    companion object {
        fun getRecommendation(rainProb: Float): RainRecommendation =
            entries.first { it.matches(rainProb) }
    }
}