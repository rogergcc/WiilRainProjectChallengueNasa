package com.rogergcc.wiilrainprojectchallenguenasa.data.weather



data class ForecastData(
    val location: Location? = Location(),
    val date: String? = "",
    val summary: Summary? = Summary(),
    val variables: List<Variable?>? = listOf(),
) {
    data class Location(
        val city: String? = "",
        val country: String? = "",
        val coordinates: Coordinates? = Coordinates()
    ) {
        data class Coordinates(
            val lat: Double? = 0.0,
            val lon: Double? = 0.0
        )
    }

    data class Summary(
        val description: String? = "",
        val recommendation: String? = ""
    )

    data class Variable(
        val id: String? = "",
        val name: String? = "",
        val unit: String? = "",
        val value: Int? = 0,
        val description: String? = "",
        val historicalAverage: HistoricalAverage? = HistoricalAverage(),
        val chartData: List<ChartData?>? = listOf()
    ) {
        data class HistoricalAverage(
            val period: String? = "",
            val averageRainfallMm: Int? = 0,
            val interpretation: String? = "",
            val averageTemperature: Double? = 0.0,
            val averageWindSpeed: Double? = 0.0
        )

        data class ChartData(
            val month: String? = "",
            val value: Int? = 0
        )
    }
}