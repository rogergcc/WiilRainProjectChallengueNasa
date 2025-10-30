package com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges

import androidx.annotation.StringRes
import com.rogergcc.wiilrainprojectchallenguenasa.R


/**
 * Created on octubre.
 * year 2025 .
 */

enum class TemperatureRange(
    val description: String,
    val color: Int,
    val range: ClosedRange<Float>,
) {
    COLD("Cold", R.color.azul, Float.NEGATIVE_INFINITY..15.0f),           // Menos de 15°C
    COMFORT("Confort", R.color.verde, 15.1f..28.0f),                 // 15°C a 28°C
    HOT("Hot", R.color.naranja, 28.1f..32.0f),                 // 28.1°C a 32°C
    EXTREME("Extreme", R.color.rojo, 32.1f..Float.MAX_VALUE);    // Más de 32°C
    companion object {
        fun fromValue(temperatura: Float): TemperatureRange {
            return entries.first { temperatura in it.range }
        }
    }
}
//│ 🌡️ CLASIFICACIÓN:
//│ 🧊 Frío: 5 (12%)
//│ 🌤 Templado: 28 (70%)
//│ 🔥 Caluroso: 7 (18%)

enum class TemperatureRecommendation(
    override val emoji: String,
    @StringRes override val textRes: Int,
    override val color: Int, // Added color field
    private val conditionRange: ClosedRange<Float>
) : Recommendation {

    COLD_WEATHER("🧊", R.string.temp_cold, R.color.light_blue, Float.NEGATIVE_INFINITY..15.0f),
    COMFORTABLE("🌤", R.string.temp_comfortable, R.color.green, 15.1f..28.0f),
    WARM_WEATHER("🌡️", R.string.temp_mild, R.color.orange, 28.1f..32.0f),
    //    HEAT_ALERT("🔥", "High temperatures recorded. Stay hydrated.", 32.1f..Float.MAX_VALUE);
    HEAT_ALERT("🔥", R.string.temp_hot, R.color.red, 32.1f..Float.MAX_VALUE);

    override fun matches(value: Float): Boolean = value in conditionRange

    companion object {
        fun getRecommendation(temperature: Float): TemperatureRecommendation =
            entries.first { it.matches(temperature) }
    }
}