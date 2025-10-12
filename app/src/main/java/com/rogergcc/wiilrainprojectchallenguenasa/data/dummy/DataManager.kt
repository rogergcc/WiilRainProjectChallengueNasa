package com.rogergcc.wiilrainprojectchallenguenasa.data.dummy

import android.content.Context
import android.util.Log
import com.rogergcc.wiilrainprojectchallenguenasa.R
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream


/**
 * Created on octubre.
 * year 2025 .
 */
class WeatherDataManager(private val context: Context) {

    fun getWeatherData(): ForecastResponse {
        //https://chat.deepseek.com/a/chat/s/c0d85f64-44ad-47df-bf89-e89b77f95af2
        val jsonFile: InputStream = context.resources.openRawResource(R.raw.nasa_dummy_data)
        return try {
            val jsonText = jsonFile.bufferedReader().use { it.readText() }
            parseWeatherJsonStringtoObject(jsonText)
        } catch (e: IOException) {
//            Timber.e(e, "Error reading JSON file")
//            Logger.getLogger("WeatherDataManager").severe("Error reading JSON file: ${e.message}")
            Log.d("TEST_LOGGER", "[WeatherDataManager] Error reading JSON file: ${e.message}")
            ForecastResponse(
                location = Location("", 0.0, 0.0),
                event_date = "",
                event_time = "",
                forecast = Forecast(false, 0, 0, "", "", ""),
                hourly_breakdown = emptyList()

            ) // O manejar el error de otra manera
        } finally {
            try {
                jsonFile.close()
            } catch (e: IOException) {
//                Timber.e(e, "Error closing InputStream")
                Log.d("TEST_LOGGER", "[WeatherDataManager] Error closing InputStream: ${e.message}")
            }
        }
    }


    enum class PokemonType {
        NORMAL, FIRE, WATER, ELECTRIC, GRASS, ICE, FIGHTING, POISON, GROUND, FLYING,
        PSYCHIC, BUG, ROCK, GHOST, DRAGON, DARK, STEEL, FAIRY
    }

    private fun parseWeatherJsonStringtoObject(jsonText: String): ForecastResponse {
        val jsonObject = JSONArray(jsonText).getJSONObject(0)

        val location = jsonObject.getJSONObject("location")
        val locationObj = Location(
            city = location.getString("city"),
            latitude = location.getDouble("latitude"),
            longitude = location.getDouble("longitude")
        )

        val forecast = jsonObject.getJSONObject("forecast")
        val forecastObj = Forecast(
            will_rain = forecast.getBoolean("will_rain"),
            confidence = forecast.getInt("confidence"),
            probability_of_precip = forecast.getInt("probability_of_precip"),
            expected_intensity = forecast.getString("expected_intensity"),
            data_source = forecast.getString("data_source"),
            next_update = forecast.getString("next_update")
        )

        val hourlyBreakdownArray = jsonObject.getJSONArray("hourly_breakdown")
        val hourlyBreakdownList = mutableListOf<HourlyBreakdown>()
        for (i in 0 until hourlyBreakdownArray.length()) {
            val item = hourlyBreakdownArray.getJSONObject(i)
            val hourlyBreakdownObj = HourlyBreakdown(
                time = item.getString("time"),
                precip_prob = item.getInt("precip_prob"),
                condition_icon = item.getString("condition_icon")
            )
            hourlyBreakdownList.add(hourlyBreakdownObj)
        }

        return ForecastResponse(
            location = locationObj,
            event_date = jsonObject.getString("event_date"),
            event_time = jsonObject.getString("event_time"),
            forecast = forecastObj,
            hourly_breakdown = hourlyBreakdownList
        )
    }

//    private fun parseJsonToPokemonList(jsonText: String): List<Pokemon> {
//        val jsonArray = JSONArray(jsonText)
//        val pokemonList = mutableListOf<Pokemon>()
//
//        for (i in 0 until jsonArray.length()) {
//            val jsonObject = jsonArray.getJSONObject(i)
//            val name = jsonObject.getString("name")
//            val number = jsonObject.getString("id")
//            val imageUrl = jsonObject.getString("imageurl")
//            val types = jsonObject.getJSONArray("typeofpokemon")
//
//            val pokemon = Pokemon(
//                i + 1, number, name, imageUrl, types.toString(),
//                0, 0, ""
//            )
//
//            pokemonList.add(pokemon)
//        }
//
//        return pokemonList
//    }
}
