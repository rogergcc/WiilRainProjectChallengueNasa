package com.rogergcc.wiilrainprojectchallenguenasa.domain.repository

import com.mapbox.geojson.Point
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.GeocodingResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.PlaceInfo


/**
 * Created on noviembre.
 * year 2025 .
 */
interface GeocodingRepository {
 suspend fun reverseGeocode(point: Point): GeocodingResult
 suspend fun reverseGeocodingPlace(lat: Double, lon: Double): PlaceInfo
}