package com.rogergcc.wiilrainprojectchallenguenasa.domain.model

import com.mapbox.geojson.Point


/**
 * Created on noviembre.
 * year 2025 .
 */
data class GeocodingResult(
    val placeName: String,
    val cityName: String,
    val coordinates: Point,
    val fullAddress: String,
)
data class PlaceInfo(
    val featureName: String,
//    val placeContextName: String?,
    val regionName: String,
    val countryName: String
)

sealed class GeocodingError : Exception() {
    object NoResults : GeocodingError()
    object NetworkError : GeocodingError()
    data class UnknownError(override val message: String) : GeocodingError()
}