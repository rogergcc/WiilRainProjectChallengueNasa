package com.rogergcc.wiilrainprojectchallenguenasa.domain

import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.ForecastResponse
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.ForecastData


/**
 * Created on octubre.
 * year 2025 .
 */
interface WeatherRepository {
    fun getWeatherData(): ForecastResponse
    fun parseWeatherData(): ForecastData
    suspend fun parseWeatherDataset(): WeatherDataset
//    fun analyzeClimateFromDataset(yearlyData: List<YearlyData>): ClimateAnalysis
    fun calculateProbabilities(yearlyData: List<YearlyData>): Map<String, Double>
}