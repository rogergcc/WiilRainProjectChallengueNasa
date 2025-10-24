package com.rogergcc.wiilrainprojectchallenguenasa.data.weather

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.ClimateAnalysis
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.ForecastResponse
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.calculateRainProbabilityFromDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.calculateTemperatureProbabilityFromDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.calculateWindProbabilityFromDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherRepository
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


/**
 * Created on octubre.
 * year 2025 .
 */
class WeatherRepositoryImpl(private val context: Context) : WeatherRepository {

    override fun getWeatherData(): ForecastResponse {
        val jsonFile: InputStream = context.resources.openRawResource(R.raw.nasa_dummy_data)
        return try {
            Gson().fromJson(
                jsonFile.bufferedReader().use { it.readText() },
                ForecastResponse::class.java
            )
        } catch (e: IOException) {
            Log.e("WeatherRepository", "Error reading JSON file", e)
            throw e
        }
    }

    override fun parseWeatherData(): ForecastData {
        val jsonText =
            context.resources.openRawResource(R.raw.weather_arequipa_2025_10_22).bufferedReader()
                .use { it.readText() }
        return Gson().fromJson(jsonText, ForecastData::class.java)
    }

    override fun parseWeatherDataset(): WeatherDataset {
        val inputStream =
            context.resources.openRawResource(R.raw.dataset_sample_location_date_weather_complete)
        val reader = InputStreamReader(inputStream)
        return Gson().fromJson(reader, WeatherDataset::class.java)
    }

    override fun analyzeClimateFromDataset(yearlyData: List<YearlyData>): ClimateAnalysis {
        val rainResult = calculateRainProbabilityFromDataset(yearlyData)
        val tempResult = calculateTemperatureProbabilityFromDataset(yearlyData)
        val windResult = calculateWindProbabilityFromDataset(yearlyData)

        val firstYear = yearlyData.minByOrNull { it.year }?.year ?: 1985
        val lastYear = yearlyData.maxByOrNull { it.year }?.year ?: 2024

        val metadata = mapOf(
            "total_years_analyzed" to rainResult.totalYears,
            "historical_period" to "$firstYear-$lastYear",
            "thresholds_used" to mapOf(
                "rain" to 1.0,
                "extreme_heat" to 32.0,
                "strong_wind" to 20.0
            ),
            "data_years_range" to "${yearlyData.minByOrNull { it.year }?.year}-${yearlyData.maxByOrNull { it.year }?.year}"
        )

        return ClimateAnalysis(rainResult, tempResult, windResult, metadata)
    }

    override fun calculateProbabilities(yearlyData: List<YearlyData>): Map<String, Double> {
        val totalYears = yearlyData.size.toDouble()

        val rainEventCount = yearlyData.count { it.rain_event }
        val heatEventCount = yearlyData.count { it.heat_event }
        val windEventCount = yearlyData.count { it.wind_event }

        val rainProbability = (rainEventCount / totalYears) * 100
        val heatProbability = (heatEventCount / totalYears) * 100
        val windProbability = (windEventCount / totalYears) * 100

        return mapOf(
            "Rain Probability" to rainProbability,
            "Heat Probability" to heatProbability,
            "Wind Probability" to windProbability
        )
    }
}