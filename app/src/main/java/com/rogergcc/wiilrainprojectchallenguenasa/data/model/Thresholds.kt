package com.rogergcc.wiilrainprojectchallenguenasa.data.model


/**
 * Created on octubre.
 * year 2025 .
 */
data class Thresholds(
    val rainLight: Double = 1.0,
    val hotExtreme: Double = 32.0,
    val windStrong: Double = 20.0,
    val unitPrecip: String = "mm",
    val unitTemp: String = "°C",
    val unitWind: String = "km/h",
)