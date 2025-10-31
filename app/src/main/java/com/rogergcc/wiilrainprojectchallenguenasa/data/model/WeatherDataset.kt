package com.rogergcc.wiilrainprojectchallenguenasa.data.model


/**
 * Created on octubre.
 * year 2025 .
 */
data class WeatherDataset(
    val metadata: Metadata,
    val summary_statistics: SummaryStatistics,
    val yearly_data: List<YearlyData>,
    val export_info: ExportInfo,
)

data class Metadata(
    val location: Location,
    val date: DateInfo,
    val historical_context: HistoricalContext,
    val thresholds_used: ThresholdsUsed,
)

data class Location(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val elevation: Double,
)

data class DateInfo(
    val target: String,
    val calculated: String,
    val period_analyzed: String,
)

data class HistoricalContext(
    val period: String,
    val n_years: Int,
    val data_source: String,
    val parameters_used: List<String>,
    val calculation_method: String,
)

data class ThresholdsUsed(
    val rain_light: Double,
    val hot_extreme: Double,
    val wind_strong: Double,
    val unit_precip: String,
    val unit_temp: String,
    val unit_wind: String,
)

data class SummaryStatistics(
    val precipitation: Precipitation,
    val temperature: Temperature,
    val wind: Wind,
    val cloud_cover: CloudCover,
)

data class Precipitation(
    val probability: Double,
    val events_detected: Int,
    val total_observations: Int,
    val average: Double,
    val maximum: Double,
    val minimum: Double,
    val interpretation: String,
)

data class Temperature(
    val probability_heat: Double,
    val events_detected: Int,
    val total_observations: Int,
    val average: Double,
    val maximum: Double,
    val minimum: Double,
    val percentile_25: Double,
    val percentile_75: Double,
    val interpretation: String,
)

data class Wind(
    val probability_strong: Double,
    val events_detected: Int,
    val total_observations: Int,
    val average: Double,
    val maximum: Double,
    val minimum: Double,
    val interpretation: String,
)

data class CloudCover(
    val average: Double,
    val interpretation: String,
)

data class YearlyData(
    val year: Int,
    val precip_mm: Double,
    val temp_c: Double,
    val wind_kmh: Double,
    val cloud_fraction: Double,
    val rain_event: Boolean,
    val heat_event: Boolean,
    val wind_event: Boolean,
)

data class ExportInfo(
    val format: String,
    val version: String,
    val generated_at: String,
    val filename: String,
)