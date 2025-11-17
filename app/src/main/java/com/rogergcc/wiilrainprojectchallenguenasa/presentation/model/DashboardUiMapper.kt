package com.rogergcc.wiilrainprojectchallenguenasa.presentation.model

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.ClimateAnalysisResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.utils.formatOneDecimalLocale


/**
 * Created on noviembre.
 * year 2025 .
 */
class DashboardUiMapper {
    fun map(dataset: WeatherDataset, analysis: ClimateAnalysisResult): DashboardUiState {
        val city = "${dataset.metadata.location.name}, ${dataset.metadata.location.country}"
        val date = dataset.metadata.date.target

        val countObservations = dataset.yearly_data.count().toString()
        val periodYearsObservation = dataset.metadata.historical_context.period

        val rain = WeatherResultMapper.map(analysis.rain)
        val temp = WeatherResultMapper.map(analysis.temperature)
        val wind = WeatherResultMapper.map(analysis.wind)

        return DashboardUiState(
            countObservations = countObservations,
            periodYearsObservation = periodYearsObservation,
            cityCountry = city,
            dateSearch = date,
            rain = rain,
            temperature = temp,
            wind = wind,
        )
    }

    class WeatherResultMapper {
        companion object {
            fun map(domainModel: com.rogergcc.wiilrainprojectchallenguenasa.domain.model.WeatherResult): WeatherResult {
                return WeatherResult(
                    average = domainModel.average,
                    probability = domainModel.probability,
                    eventYears = domainModel.eventYears,
                    totalYears = domainModel.totalYears,
                    minValue = domainModel.minValue,
                    maxValue = domainModel.maxValue,
                    weatherType = domainModel.weatherType,
                    recomendation = domainModel.recomendation
                )
            }
        }
    }

    companion object {

        fun averageText(value: Double, unit: String): String {
            val avg = value.formatOneDecimalLocale()
            return "$avg $unit"
        }

        fun probabilityText(value: Double): String {
//        val percent = kotlin.math.round(value).toInt()
            val percent = value.formatOneDecimalLocale()
            return "$percent %"
        }
    }
}