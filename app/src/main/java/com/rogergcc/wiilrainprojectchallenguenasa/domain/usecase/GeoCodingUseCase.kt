package com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase

import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.PlaceInfo
import com.rogergcc.wiilrainprojectchallenguenasa.domain.repository.GeocodingRepository


/**
 * Created on noviembre.
 * year 2025 .
 */
class GeoCodingUseCase(
    private val geocodingRepository: GeocodingRepository
) {
    suspend fun placeInfoFromCoordinates(lat: Double, lon: Double): PlaceInfo {
        return geocodingRepository.reverseGeocodingPlace(lat, lon)
    }
}