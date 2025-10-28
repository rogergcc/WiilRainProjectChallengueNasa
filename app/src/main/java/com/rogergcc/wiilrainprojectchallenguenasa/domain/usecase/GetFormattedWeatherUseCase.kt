package com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherFormatter
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherRepository
import com.rogergcc.wiilrainprojectchallenguenasa.domain.mapper.WeatherRecordMapper


/**
 * Created on octubre.
 * year 2025 .
 */
class GetFormattedWeatherUseCase(
    private val weatherDataset: WeatherDataset,
    private val formatter: WeatherFormatter,
    private val weatherRecordMapper: WeatherRecordMapper
) {
    suspend operator fun invoke(type: WeatherType): String {
        val mapper= weatherRecordMapper.map(weatherDataset)
        return formatter.formatWeather(type, mapper)
    }
}
