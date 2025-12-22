package com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges

import androidx.annotation.StringRes
import com.rogergcc.wiilrainprojectchallenguenasa.R


/**
 * Created on octubre.
 * year 2025 .
 */

//│ 🌡️ CLASIFICACIÓN:
//│ 🧊 Muy Frio: 5 (12%)
//│ ❄️ Frio: 10 (25%)
//│ 🌤️ Templado: 15 (38%)
//│ ☀️ Calido: 20 (50%)
//│ 🔥 Muy Calido: 25 (62%)


enum class TemperatureRecommendation(
    override val conditionRange: ClosedRange<Float>,
    override val emoji: String,
    override val color: Int,
    @StringRes override val labelRes: Int,
    @StringRes override val descRes: Int
) : Recommendation {

//    COLD_WEATHER("🧊", R.string.temp_cold, R.color.light_blue, Float.NEGATIVE_INFINITY..15.0f),
//    COMFORTABLE("🌤", R.string.temp_comfortable, R.color.green, 15.1f..28.0f),
//    WARM_WEATHER("🌡️", R.string.temp_mild, R.color.orange, 28.1f..32.0f),
//    //    HEAT_ALERT("🔥", "High temperatures recorded. Stay hydrated.", 32.1f..Float.MAX_VALUE);
//    HEAT_ALERT("🔥", R.string.temp_hot, R.color.red, 32.1f..Float.MAX_VALUE);

    TEMP_VERY_COLD(-999f ..  0.9f, "🧊", R.color.temp_very_cold, R.string.temp_very_cold_label, R.string.temp_very_cold_desc),
    TEMP_COLD(1f .. 10.9f, "❄️", R.color.temp_cold, R.string.temp_cold_label, R.string.temp_cold_desc),
    TEMP_MILD(11f .. 20.9f, "🌤️", R.color.temp_mild, R.string.temp_mild_label, R.string.temp_mild_desc),
    TEMP_WARM(21f .. 30.9f, "☀️", R.color.temp_warm, R.string.temp_warm_label, R.string.temp_warm_desc),
    TEMP_HOT(31f.. 999f, "🔥", R.color.temp_hot, R.string.temp_hot_label, R.string.temp_hot_desc);

    override fun matches(value: Float): Boolean = value in conditionRange

    companion object {
        fun getRecommendation(temperature: Float): TemperatureRecommendation =
            entries.first { it.matches(temperature) }
    }
}