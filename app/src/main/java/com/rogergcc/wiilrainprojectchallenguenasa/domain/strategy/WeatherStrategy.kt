package com.rogergcc.wiilrainprojectchallenguenasa.domain.strategy

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.WeatherResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.utils.calculateWeatherResult


/**
 * Created on octubre.
 * year 2025 .
 */
interface WeatherStrategy {
    fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult
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

class GenericWeatherStrategy(
    private val selector: (YearlyData) -> Double
) : WeatherStrategy {
    override fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult {
        val values = yearlyData.map(selector)
        return calculateWeatherResult(values, weatherType)
    }
}


object WeatherStrategyFactory {
    fun getStrategy(weatherType: WeatherType): WeatherStrategy {
        return when (weatherType) {
            WeatherType.RAIN -> GenericWeatherStrategy { it.precip_mm }
            WeatherType.TEMP -> GenericWeatherStrategy { it.temp_c }
            WeatherType.WIND -> GenericWeatherStrategy { it.wind_kmh }
        }
    }
}