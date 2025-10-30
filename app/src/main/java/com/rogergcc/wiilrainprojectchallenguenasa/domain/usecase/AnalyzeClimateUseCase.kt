package com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.ClimateAnalysisResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.strategy.WeatherStrategyFactory


/**
 * Created on octubre.
 * year 2025 .
 */
class AnalyzeClimateUseCase {
    operator fun invoke(yearlyData: List<YearlyData>): ClimateAnalysisResult {
        val rainResult = WeatherStrategyFactory.getStrategy(WeatherType.RAIN)
            .calculate(yearlyData, WeatherType.RAIN)

        val temperatureResult = WeatherStrategyFactory.getStrategy(WeatherType.TEMP)
            .calculate(yearlyData, WeatherType.TEMP)

        val windResult = WeatherStrategyFactory.getStrategy(WeatherType.WIND)
            .calculate(yearlyData, WeatherType.WIND)

        return ClimateAnalysisResult(
            rain = rainResult,
            temperature = temperatureResult,
            wind = windResult
        )
    }
}