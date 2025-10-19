package com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges

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
    val emoji: String,
    val message: String,
    val condition: (Float, ) -> Boolean,
) {
    HEAT_ALERT("🔥","High", { temp -> temp > 32 }),
    WARM_WEATHER("🌤","Warm ", { temp -> temp in 28.1f..32.0f }),
    COMFORTABLE("🧊","Comfortable ", { temp -> temp in 15.1f..28.0f }),
    COLD_WEATHER("🧊","Cold ", { temp -> temp <= 15.0f });
}