package com.rogergcc.wiilrainprojectchallenguenasa.domain

import com.rogergcc.wiilrainprojectchallenguenasa.data.WeatherResult
import com.rogergcc.wiilrainprojectchallenguenasa.data.calculateWeatherResult
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData


/**
 * Created on octubre.
 * year 2025 .
 */
interface WeatherStrategy {
    fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult
}

class RainStrategy : WeatherStrategy {
    override fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult {
        val precipitations = yearlyData.map { it.precip_mm }
        return calculateWeatherResult(precipitations, weatherType.extremeValue, weatherType.unit)
    }
}

class TemperatureStrategy : WeatherStrategy {
    override fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult {
        val temperatures = yearlyData.map { it.temp_c }
        return calculateWeatherResult(temperatures, weatherType.extremeValue, weatherType.unit)
    }
}

class WindStrategy : WeatherStrategy {
    override fun calculate(yearlyData: List<YearlyData>, weatherType: WeatherType): WeatherResult {
        val windSpeeds = yearlyData.map { it.wind_kmh }
        return calculateWeatherResult(windSpeeds, weatherType.extremeValue, weatherType.unit)
    }
}

object WeatherStrategyFactory {
    fun getStrategy(weatherType: WeatherType): WeatherStrategy {
        return when (weatherType) {
            WeatherType.RAIN -> RainStrategy()
            WeatherType.TEMP -> TemperatureStrategy()
            WeatherType.WIND -> WindStrategy()
        }
    }
}