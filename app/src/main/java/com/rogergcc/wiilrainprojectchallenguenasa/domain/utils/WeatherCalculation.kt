package com.rogergcc.wiilrainprojectchallenguenasa.domain.utils

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.WeatherResult


/**
 * Created on octubre.
 * year 2025 .
 */


fun calculateWeatherResult(
    yearlyData: List<Double>,
    weatherType: WeatherType,
    recommendationProvider: (Double) -> Recommendation,

    ): WeatherResult {
    val totalYears = yearlyData.size
    val eventYears = yearlyData.count { it > weatherType.extremeValue }
    val probability = (eventYears.toDouble() / totalYears) * 100.0
    val minValue = yearlyData.calculateMinOrZero()
    val maxValue = yearlyData.calculateMaxOrZero()
    val average = yearlyData.calculateAverageOrZero()

//    val interpretation = buildInterpretation(
//        threshold = weatherType.extremeValue,
//        probability = probability,
//        minValue = minValue,
//        maxValue = maxValue,
//        unit = weatherType.unit
//    )

//    🌀 1️⃣ Diferencia conceptual entre las tres métricas
//
//    | Métrica                 | Qué representa                | Qué mide exactamente                                       | Ejemplo de interpretación         |
//    | ----------------------- | ----------------------------- | ---------------------------------------------------------- | --------------------------------- |
//    | **rain_probability**    | Probabilidad de lluvia        | Porcentaje (%) de días con lluvia o eventos de lluvia      | 70% → alta probabilidad de lluvia |
//    | **temperature_average** | Temperatura promedio diaria   | Valor medio (°C) de las temperaturas registradas en el día | 25°C → día cálido                 |
//    | **wind_speed_average**  | Velocidad promedio del viento | Valor medio (km/h) del viento medido durante el día        | 20 km/h → viento moderado         |
//    💡 En resumen:
//
//    🌧️ rain_probability → mide probabilidad (valor porcentual)
//
//    🌡️ temperature_average → mide promedio (valor continuo en °C)
//
//    💨 wind_speed_average → mide promedio (valor continuo en km/h)

//    🧩 En resumen:
//    | Métrica                     | Tipo de variable         | Qué representa              | Justificación del nombre                                         |
//    | --------------------------- | ------------------------ | --------------------------- | ---------------------------------------------------------------- |
//    | 🌧️ **rain_probability**    | Probabilística (0–100 %) | Chance de que ocurra lluvia | No es una magnitud promedio, sino una **probabilidad** de evento |
//    | 🌡️ **temperature_average** | Continua (°C)            | Promedio diario             | Variable física promedio                                         |
//    | 💨 **wind_speed_average**   | Continua (km/h)          | Promedio diario             | Variable física promedio                                         |


    // Obtain recommendation based on average value generic form for each weather type Enum
    val recommendation: Recommendation = recommendationProvider(average)
//    val recommandationString = buildRecommendation(
//       recommendations = recommendationType ,
//        valueSelector = average,
//        condition = condition
//    )

    return WeatherResult(
        average = average,
        probability = probability,
        eventYears = eventYears,
        totalYears = totalYears,
        minValue = minValue,
        maxValue = maxValue,
//        interpretation = interpretation,
        weatherType = weatherType,
        recomendation = recommendation
    )
}

fun <T> buildRecommendation(
    recommendations: Iterable<T>,
    valueSelector: Double,
    condition: (T, Double) -> Boolean,
): T? where T : Enum<T>, T : Recommendation {
    val matchedRecommendation = recommendations.find { rec ->
        condition(rec, valueSelector)
    }
    return matchedRecommendation
}


private fun buildInterpretation(
    threshold: Double,
    probability: Double,
    minValue: Double,
    maxValue: Double,
    unit: String,
): String = buildString {
    append("Probabilidad (>${threshold}${unit}): ${probability.formatTwoDecimalLocale()}%\n")
    append("Mínimo Histórico: ${minValue.formatTwoDecimalLocale()}${unit}\n")
    append("Máximo Histórico: ${maxValue.formatTwoDecimalLocale()}${unit}")
}