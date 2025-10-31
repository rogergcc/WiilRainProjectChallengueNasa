package com.rogergcc.wiilrainprojectchallenguenasa.domain.strategy

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.WeatherResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.utils.calculateWeatherResult


/**
 * Created on octubre.
 * year 2025 .
 */
interface WeatherStrategy {
    fun calculate(
        yearlyData: List<YearlyData>,
        weatherType: WeatherType,

        ): WeatherResult
}

//class RainStrategy : WeatherStrategy {
//    override fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult {
//        val precipitations = yearlyData.map { it.precip_mm }
//        return calculateWeatherResult(
//            precipitations,
//            weatherType)
//    }
//}
//
//class TemperatureStrategy : WeatherStrategy {
//    override fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult {
//        val temperatures = yearlyData.map { it.temp_c }
//        return calculateWeatherResult(
//            temperatures,
//            weatherType
//        )
//    }
//}
//
//class WindStrategy : WeatherStrategy {
//    override fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult {
//        val windSpeeds = yearlyData.map { it.wind_kmh }
//        return calculateWeatherResult(
//            windSpeeds,
//            weatherType
//        )
//    }
//}

class GenericWeatherStrategy<T : Recommendation>(
    private val selector: (YearlyData) -> Double,
    private val recommendationProvider: (Double) -> T,
) : WeatherStrategy {
    override fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult {
        val values = yearlyData.map(selector)
        val result = calculateWeatherResult(values, weatherType, recommendationProvider)
//        val recommendation:Recommendation = recommendationProvider(result.average)
//        result.recomendation = recommendation
//        val resultModi = result.copy(recomendation = recommendation)

        return result
    }
}


object WeatherStrategyFactory {
    fun getStrategy(weatherType: WeatherType): WeatherStrategy {
        return when (weatherType) {
            WeatherType.RAIN -> GenericWeatherStrategy(
                selector = { it.precip_mm },
                recommendationProvider = { RainRecommendation.getRecommendation(it.toFloat()) }
            )

            WeatherType.TEMP -> GenericWeatherStrategy(
                selector = { it.temp_c },
                recommendationProvider = { TemperatureRecommendation.getRecommendation(it.toFloat()) }
            )

            WeatherType.WIND -> GenericWeatherStrategy(
                selector = { it.wind_kmh },
                recommendationProvider = { WindRecommendation.getRecommendation(it.toFloat()) }
            )
        }
    }
}