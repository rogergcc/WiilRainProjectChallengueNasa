package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.WeatherYearRecord
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.Thresholds
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType


/**
 * Created on octubre.
 * year 2025 .
 */
class WeatherDetailViewModel(
 private val weatherDataset: WeatherDataset,
    private val weatherFormatter: WeatherFormatter
) : ViewModel() {

    private val thresholds = Thresholds()

    private fun weatherYearRecordFormated(): List<WeatherYearRecord> {
        return weatherDataset.yearly_data.map { yearRecord ->
            WeatherYearRecord(
                year = yearRecord.year,
                precip_mm = yearRecord.precip_mm,
                temp_c = yearRecord.temp_c,
                wind_kmh = yearRecord.wind_kmh,
                cloud_fraction = yearRecord.cloud_fraction,
                thresholds = thresholds
            )
        }
    }

//    fun getWeatherText(weatherType: WeatherType): String {
//        return when (weatherType) {
//            WeatherType.RAIN -> WeatherFormatter.formatRain(weatherYearRecordFormated(),WeatherType.RAIN.description, thresholds)
//            WeatherType.TEMP -> WeatherFormatter.formatTemp(weatherYearRecordFormated(),WeatherType.TEMP.description, thresholds)
//            WeatherType.WIND -> WeatherFormatter.formatWind(weatherYearRecordFormated(),WeatherType.WIND.description, thresholds)
//        }
//    }
    fun getFormattedWeather(weatherType: WeatherType): String {
        val formattedData = weatherYearRecordFormated() // Calcula la lista una sola vez
        return weatherFormatter.formatWeather(weatherType, formattedData, thresholds)
    }

//    fun getRainText() = WeatherFormatter.formatRain(weatherYearRecordFormated(), thresholds)
//    fun getTempText() = WeatherFormatter.formatTemp(weatherYearRecordFormated(), thresholds)
//    fun getWindText() = WeatherFormatter.formatWind(weatherYearRecordFormated(), thresholds)
}

class WeatherDetailViewModelFactory(
    private val weatherDataset: WeatherDataset,
    private val weatherFormatter: WeatherFormatter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherDetailViewModel(weatherDataset,weatherFormatter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}