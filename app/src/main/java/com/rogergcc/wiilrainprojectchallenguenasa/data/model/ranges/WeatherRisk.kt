package com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges

import com.rogergcc.wiilrainprojectchallenguenasa.R


/**
 * Created on octubre.
 * year 2025 .
 */

interface Recommendation {
    val emoji: String
    val textRes: Int
    fun matches(value: Float): Boolean
}


// Enum para los rangos de lluvia
enum class RainRisk(val threshold: Double, val label: String, val color: Int) {
    LOW(1.0, "Bajo riesgo", R.color.verde),
    MODERATE(5.0, "Riesgo moderado", R.color.amarillo),
    HIGH(10.0, "Alto riesgo", R.color.rojo)
}

// Enum para los rangos de temperatura
enum class TempRisk(val threshold: Double, val label: String, val color: Int) {
    COLD(0.0, "Frío", R.color.azul),
    COMFORTABLE(28.0, "Confort", R.color.verde),
    HOT(32.0, "Calor", R.color.naranja),
    EXTREME(40.0, "Extremo", R.color.rojo)
}

// Enum para los rangos de viento
enum class WindRisk(val threshold: Double, val label: String, val color: Int) {
    CALM(5.0, "Riesgo bajo", R.color.verde),
    MODERATE(20.0, "Riesgo moderado", R.color.amarillo),
    STRONG(50.0, "Riesgo fuerte", R.color.rojo)
}