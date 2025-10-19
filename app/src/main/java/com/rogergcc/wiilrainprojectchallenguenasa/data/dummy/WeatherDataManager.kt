package com.rogergcc.wiilrainprojectchallenguenasa.data.dummy

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.ForecastData
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * Created on octubre.
 * year 2025 .
 */
class WeatherDataManager(private val context: Context) {

    fun getWeatherData(): ForecastResponse {
        //https://chat.deepseek.com/a/chat/s/c0d85f64-44ad-47df-bf89-e89b77f95af2
        val jsonFile: InputStream = context.resources.openRawResource(R.raw.nasa_dummy_data)
         try {
            return Gson().fromJson(jsonFile.bufferedReader().use { it.readText() }, ForecastResponse::class.java)
        }
        catch (e: IOException) {
            Log.e("WeatherDataManager", "Error reading JSON file", e)
            // Manejar el error de manera adecuada, por ejemplo, lanzar una excepción o retornar un valor por defecto
            throw e // O retornar un valor por defecto
        }
    }

    fun parseWeatherData(): ForecastData {
//        val jsonText = context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        val jsonText = context.resources.openRawResource(R.raw.weather_arequipa_2025_10_22).bufferedReader().use { it.readText() }

        return Gson().fromJson(jsonText, ForecastData::class.java)
    }

    fun parseWeatherDataset(): WeatherDataset {
        val inputStream = context.resources.openRawResource(R.raw.dataset_sample_location_date_weather_complete)
        val reader = InputStreamReader(inputStream)
        return Gson().fromJson(reader, WeatherDataset::class.java)
    }

    fun analyzeClimateFromDataset(yearlyData: List<YearlyData>): ClimateAnalysis {
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

    fun calculateProbabilities(yearlyData: List<YearlyData>): Map<String, Double> {
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
