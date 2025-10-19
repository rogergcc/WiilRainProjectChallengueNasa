package com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges

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
    val emoji: String,
    val message: String,
    val condition: (Float) -> Boolean
) {
    NO_RAIN("☁️","No rain expected ", { rainProb -> rainProb < 20f }),
    LIGHT_RAIN("🌦️","Light rain possible ", { rainProb -> rainProb in 20f..40f }),
    MODERATE_RAIN("🌧️","Moderate rain likely ", { rainProb -> rainProb in 40.1f..60f }),
    HEAVY_RAIN("⛈️","Heavy rain expected ", { rainProb -> rainProb > 60.1f });
}