package com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherFormatter
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherRepository
import com.rogergcc.wiilrainprojectchallenguenasa.domain.mapper.WeatherRecordMapper


/**
 * Created on octubre.
 * year 2025 .
 */
class GetFormattedWeatherUseCase(
    private val weatherRepository: WeatherRepository,
    private val weatherFormatter: WeatherFormatter,
    private val weatherRecordMapper: WeatherRecordMapper,
) {
    suspend operator fun invoke(type: WeatherType): String {
        val weatherDataset= weatherRepository.parseWeatherDataset()
        val mapper= weatherRecordMapper.map(weatherDataset)
        return weatherFormatter.formatWeather(type, mapper)
    }
}
