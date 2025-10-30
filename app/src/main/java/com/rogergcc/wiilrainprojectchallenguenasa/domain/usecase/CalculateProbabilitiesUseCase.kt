package com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase

import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherRepository


/**
 * Created on octubre.
 * year 2025 .
 */
class CalculateProbabilitiesUseCase(
    private val repository: WeatherRepository
) {
//    fun execute(dataset: WeatherDataset): Map<String, Double> {
//        val yearlyData = dataset.yearly_data
//        return mapOf(
//            "rainProbability" to repository.analyzeClimateFromDataset(yearlyData).rain.probability,
//            "temperatureProbability" to repository.analyzeClimateFromDataset(yearlyData).temperature.heatProbability,
//            "windProbability" to repository.analyzeClimateFromDataset(yearlyData).wind.strongWindProbability
//        )
//    }
}