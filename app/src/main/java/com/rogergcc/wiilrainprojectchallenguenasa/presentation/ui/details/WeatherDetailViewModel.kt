package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.Thresholds
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset


/**
 * Created on octubre.
 * year 2025 .
 */
class WeatherDetailViewModel(
 private val weatherDataset: WeatherDataset
) : ViewModel() {

    private val thresholds = Thresholds()

    private fun weatherYearRecordFormated(): List<WeatherYearRecord> {

        val yearlyData = weatherDataset.yearly_data.map { yearRecord ->
            WeatherYearRecord(
                year = yearRecord.year,
                precip_mm = yearRecord.precip_mm,
                temp_c = yearRecord.temp_c,
                wind_kmh = yearRecord.wind_kmh,
                cloud_fraction = yearRecord.cloud_fraction,
                rain_event = yearRecord.precip_mm > thresholds.rainLight,
                heat_event = yearRecord.temp_c > thresholds.hotExtreme,
                wind_event = yearRecord.wind_kmh > thresholds.windStrong,
            )
        }
        return yearlyData

    }

    fun getWeatherText(weatherType: WeatherType): String {
        return when (weatherType) {
            WeatherType.RAIN -> WeatherFormatter.formatRain(weatherYearRecordFormated(),WeatherType.RAIN.description, thresholds)
            WeatherType.TEMP -> WeatherFormatter.formatTemp(weatherYearRecordFormated(),WeatherType.TEMP.description, thresholds)
            WeatherType.WIND -> WeatherFormatter.formatWind(weatherYearRecordFormated(),WeatherType.WIND.description, thresholds)
        }
    }

//    fun getRainText() = WeatherFormatter.formatRain(weatherYearRecordFormated(), thresholds)
//    fun getTempText() = WeatherFormatter.formatTemp(weatherYearRecordFormated(), thresholds)
//    fun getWindText() = WeatherFormatter.formatWind(weatherYearRecordFormated(), thresholds)
}

class WeatherDetailViewModelFactory(
    private val weatherDataset: WeatherDataset
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherDetailViewModel(weatherDataset) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}